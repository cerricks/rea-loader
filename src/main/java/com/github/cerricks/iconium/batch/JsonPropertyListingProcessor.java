/*
 * Copyright 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cerricks.iconium.batch;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.cerricks.iconium.data.Event;
import com.github.cerricks.iconium.data.Property;
import com.github.cerricks.iconium.data.PropertyListing;
import com.github.cerricks.iconium.data.School;
import com.github.cerricks.iconium.util.JsonParseUtil;
import java.io.IOException;
import java.util.Iterator;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Converts a {@link JsonNode} with {@code "_type"="RealEstateSoldHistoryItem"}
 * to a {@link JsonPropertyListing}.
 *
 * @author Clifford Errickson
 */
@Component
@StepScope
public class JsonPropertyListingProcessor implements ItemProcessor<JsonNode, JsonPropertyListing> {

    public JsonPropertyListingProcessor() {
    }

    @Override
    public JsonPropertyListing process(final JsonNode item) throws Exception {
        if (!item.isObject()) {
            throw new IllegalStateException("item is not a valid JSON object");
        }

        if (!item.has("_type")
                || ((item.hasNonNull("_type") && !item.get("_type").asText().equals("RealEstateSoldHistoryItem")))) {
            return null; // filters out item with invalid type
        }

        return processPropertyListing(item);
    }

    /**
     * Maps {@link JsonNode} to a {@link PropertyListing}.
     *
     * @param node {@link JsonNode} to convert
     * @return {@link PropertyListing} represented by the {@link JsonNode}.
     * @throws IOException on IO error
     */
    private JsonPropertyListing processPropertyListing(final JsonNode node) throws IOException {
        JsonPropertyListing listing = new JsonPropertyListing(node);

        // process root attributes
        listing.setType(JsonParseUtil.parseText(node, "_type"));
        listing.setUrl(JsonParseUtil.parseText(node, "url"));
        listing.setCrawlDate(JsonParseUtil.parseLocalDate(node, "crawl_date", JsonParseUtil.SHORT_DATE_FORMATTER));
        listing.setCrawlDateTime(JsonParseUtil.parseDateTime(node, "crawl_datetime", JsonParseUtil.SHORT_DATE_TIME_FORMATTER));
        listing.setInputAddress(JsonParseUtil.parseText(node, "input_address"));
        listing.setCachedPageId(JsonParseUtil.parseText(node, "_cached_page_id"));
        listing.getPropertyDetails().setAddressPID(JsonParseUtil.parseText(node, "addr_id"));
        listing.getPropertyDetails().setPriceEstimateFrom(JsonParseUtil.parseInteger(node, "price_estimation_to"));
        listing.getPropertyDetails().setPriceEstimateTo(JsonParseUtil.parseInteger(node, "price_estimation_from"));
        listing.getPropertyDetails().setPriceEstimateConfidence(JsonParseUtil.parseText(node, "price_estimation_confidence"));

        // process 'about' section
        if (node.has("about")) {
            JsonNode aboutNode = node.get("about");

            if (aboutNode != null) {
                listing.getPropertyDetails().setBedrooms(JsonParseUtil.parseInteger(aboutNode, "Bedrooms"));
                listing.getPropertyDetails().setBathrooms(JsonParseUtil.parseInteger(aboutNode, "Bathrooms"));
                listing.getPropertyDetails().setCarSpots(JsonParseUtil.parseInteger(aboutNode, "Car"));
                listing.getPropertyDetails().setCouncilArea(JsonParseUtil.parseText(aboutNode, "Council area"));
                listing.getPropertyDetails().setBlockCode(JsonParseUtil.parseText(aboutNode, "Section/Block"));
                listing.getPropertyDetails().setYearBuilt(JsonParseUtil.parseText(aboutNode, "Year built"));
                listing.getPropertyDetails().setBuildingSizeDesc(JsonParseUtil.parseText(aboutNode, "Building area"));
                listing.getPropertyDetails().setLandSizeDesc(JsonParseUtil.parseText(aboutNode, "Land size"));
                listing.getPropertyDetails().setLotPlan(JsonParseUtil.parseText(aboutNode, "Lot/Plan"));
                listing.getPropertyDetails().setPropertyType(JsonParseUtil.parseText(aboutNode, "Property type"));
            }
        }

        // process "schools" section
        if (node.has("schools")) {
            JsonNode schoolsNode = node.get("schools");

            if (schoolsNode != null) {
                Iterator<JsonNode> it = schoolsNode.iterator();

                while (it.hasNext()) {
                    JsonNode schoolNode = it.next();

                    School school = new School();

                    school.setName(JsonParseUtil.parseText(schoolNode, "name"));
                    school.setType(JsonParseUtil.parseText(schoolNode, "school_type"));
                    school.setWebsite(JsonParseUtil.parseText(schoolNode, "website"));
                    school.setSector(JsonParseUtil.parseText(schoolNode, "sector"));
                    school.setLocality(JsonParseUtil.parseText(schoolNode, "suburb"));
                    school.setState(JsonParseUtil.parseText(schoolNode, "state"));
                    school.setStreet(JsonParseUtil.parseText(schoolNode, "street"));
                    school.setPostCode(JsonParseUtil.parseText(schoolNode, "postcode"));
                    school.setDistance(JsonParseUtil.parseText(schoolNode, "distance"));

                    listing.getPropertyDetails().addNearbySchool(school);
                }
            }
        }

        // process "comparable_properties" section
        if (node.has("comparable_properties")) {
            JsonNode comparablePropertiesNode = node.get("comparable_properties");

            if (comparablePropertiesNode != null) {
                // process "for_sale_properties" section
                if (comparablePropertiesNode.has("for_sale_properties")) {
                    JsonNode forSalePropertiesNode = comparablePropertiesNode.get("for_sale_properties");

                    Iterator<JsonNode> it = forSalePropertiesNode.iterator();

                    while (it.hasNext()) {
                        listing.getPropertyDetails().addComporablePropertyForSale(processComparableProperty(it.next()));
                    }
                }

                // process "for_rent_properties" section
                if (comparablePropertiesNode.has("for_rent_properties")) {
                    JsonNode forRentPropertiesNode = comparablePropertiesNode.get("for_rent_properties");

                    Iterator<JsonNode> it = forRentPropertiesNode.iterator();

                    while (it.hasNext()) {
                        listing.getPropertyDetails().addComparablePropertyForRent(processComparableProperty(it.next()));
                    }
                }

                // process "sold_properties" section
                if (comparablePropertiesNode.has("sold_properties")) {
                    JsonNode soldPropertiesNode = comparablePropertiesNode.get("sold_properties");

                    Iterator<JsonNode> it = soldPropertiesNode.iterator();

                    while (it.hasNext()) {
                        listing.getPropertyDetails().addComparablePropertySold(processComparableProperty(it.next()));
                    }
                }
            }
        }

        // process "history" section
        if (node.has("history")) {
            JsonNode historyNode = node.get("history");

            if (historyNode != null) {
                Iterator<JsonNode> it = historyNode.iterator();

                while (it.hasNext()) {
                    JsonNode eventNode = it.next();

                    Event event = new Event();
                    event.setYearMonth(JsonParseUtil.parseYearMonth(eventNode, "date", JsonParseUtil.MEDIUM_YEAR_MONTH_FORMATTER));
                    event.setType(JsonParseUtil.parseText(eventNode, "rent_or_sold"));
                    event.setPriceDesc(JsonParseUtil.parseText(eventNode, "price"));
                    event.setAgency(JsonParseUtil.parseText(eventNode, "agency"));

                    listing.getPropertyDetails().addEvent(event);
                }
            }
        }

        return listing;
    }

    /**
     * Helper method for processing comparable properties.
     *
     * @param node {@link JsonNode} containing the comparable property details.
     * @return comparable property details
     * @throws IOException on IO error
     */
    private Property processComparableProperty(final JsonNode node) throws IOException {
        Property property = new Property();

        property.setSoldDate(JsonParseUtil.parseLocalDate(node, "sold_date", JsonParseUtil.MEDIUM_DATE_FORMATTER));
        property.setBedrooms(JsonParseUtil.parseInteger(node, "bedrooms"));
        property.setBathrooms(JsonParseUtil.parseInteger(node, "bathrooms"));
        property.setCarSpots(JsonParseUtil.parseInteger(node, "garages"));
        property.setPriceDesc(JsonParseUtil.parseText(node, "price"));
        property.setLocality(JsonParseUtil.parseText(node, "suburb"));
        property.setState(JsonParseUtil.parseText(node, "state"));
        property.setPostCode(JsonParseUtil.parseText(node, "postcode"));
        property.setAddress(JsonParseUtil.parseText(node, "address"));
        property.setSaleMethod(JsonParseUtil.parseText(node, "authority_type"));

        if (node.has("date_available")) {
            if ("Available now".equalsIgnoreCase(node.get("date_available").asText(""))) {
                property.setAvailableNow(true);
            } else {
                property.setAvailableForLeaseDate(JsonParseUtil.parseLocalDate(node, "date_available", JsonParseUtil.MEDIUM_DATE_FORMATTER));
            }
        }

        return property;
    }

}
