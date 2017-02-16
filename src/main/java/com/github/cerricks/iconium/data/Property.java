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
package com.github.cerricks.iconium.data;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;

/**
 * Contains details on a real estate property.
 *
 * @author Clifford Errickson
 */
public class Property {

    private Long id;
    private LocalDate recordDate;
    private String addressPID;
    private String propertyType;
    private String yearBuilt;
    private String state;
    private String locality;
    private String postCode;
    private String address;
    private String councilArea;
    private String blockCode;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer carSpots;
    private String landSizeDesc;
    private String buildingSizeDesc;
    private String lotPlan;
    private String priceDesc;
    private String saleMethod;
    private LocalDate soldDate;
    private LocalDate availableForLeaseDate;
    private boolean availableNow;
    private Integer priceEstimateFrom;
    private Integer priceEstimateTo;
    private String priceEstimateConfidence;

    private final List<Property> comparablePropertiesForSale;
    private final List<Property> comparablePropertiesForRent;
    private final List<Property> comparablePropertiesSold;
    private final List<School> nearbySchools;
    private final List<Event> history;

    public Property() {
        this.comparablePropertiesForSale = new ArrayList();
        this.comparablePropertiesForRent = new ArrayList();
        this.comparablePropertiesSold = new ArrayList();
        this.nearbySchools = new ArrayList();
        this.history = new ArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public String getAddressPID() {
        return addressPID;
    }

    public void setAddressPID(String addressPID) {
        this.addressPID = addressPID;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(String yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCouncilArea() {
        return councilArea;
    }

    public void setCouncilArea(String councilArea) {
        this.councilArea = councilArea;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getCarSpots() {
        return carSpots;
    }

    public void setCarSpots(Integer carSpots) {
        this.carSpots = carSpots;
    }

    public String getLandSizeDesc() {
        return landSizeDesc;
    }

    public void setLandSizeDesc(String landSizeDesc) {
        this.landSizeDesc = landSizeDesc;
    }

    public String getBuildingSizeDesc() {
        return buildingSizeDesc;
    }

    public void setBuildingSizeDesc(String buildingSizeDesc) {
        this.buildingSizeDesc = buildingSizeDesc;
    }

    public String getLotPlan() {
        return lotPlan;
    }

    public void setLotPlan(String lotPlan) {
        this.lotPlan = lotPlan;
    }

    public String getPriceDesc() {
        return priceDesc;
    }

    public void setPriceDesc(String priceDesc) {
        this.priceDesc = priceDesc;
    }

    public String getSaleMethod() {
        return saleMethod;
    }

    public void setSaleMethod(String saleMethod) {
        this.saleMethod = saleMethod;
    }

    public LocalDate getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(LocalDate soldDate) {
        this.soldDate = soldDate;
    }

    public boolean getAvailableNow() {
        return availableNow;
    }

    public void setAvailableNow(boolean availableNow) {
        this.availableNow = availableNow;
    }

    public LocalDate getAvailableForLeaseDate() {
        return availableForLeaseDate == null && availableNow
                ? new LocalDate(recordDate)
                : availableForLeaseDate;
    }

    public void setAvailableForLeaseDate(LocalDate availableForLeaseDate) {
        this.availableForLeaseDate = availableForLeaseDate;
    }

    public Integer getPriceEstimateFrom() {
        return priceEstimateFrom;
    }

    public void setPriceEstimateFrom(Integer priceEstimateFrom) {
        this.priceEstimateFrom = priceEstimateFrom;
    }

    public Integer getPriceEstimateTo() {
        return priceEstimateTo;
    }

    public void setPriceEstimateTo(Integer priceEstimateTo) {
        this.priceEstimateTo = priceEstimateTo;
    }

    public String getPriceEstimateConfidence() {
        return priceEstimateConfidence;
    }

    public void setPriceEstimateConfidence(String priceEstimateConfidence) {
        this.priceEstimateConfidence = priceEstimateConfidence;
    }

    public void addComporablePropertyForSale(Property property) {
        if (property != null) {
            this.comparablePropertiesForSale.add(property);
        }
    }

    public List<Property> getComparablePropertiesForSale() {
        return comparablePropertiesForSale;
    }

    public void addComparablePropertyForRent(Property property) {
        if (property != null) {
            this.comparablePropertiesForRent.add(property);
        }
    }

    public List<Property> getComparablePropertiesForRent() {
        return comparablePropertiesForRent;
    }

    public void addComparablePropertySold(Property property) {
        if (property != null) {
            this.comparablePropertiesSold.add(property);
        }
    }

    public List<Property> getComparablePropertiesSold() {
        return comparablePropertiesSold;
    }

    public void addNearbySchool(School school) {
        if (school != null) {
            this.nearbySchools.add(school);
        }
    }

    public List<School> getNearbySchools() {
        return nearbySchools;
    }

    public void addEvent(Event event) {
        if (event != null) {
            this.history.add(event);
        }
    }

    public List<Event> getHistory() {
        return history;
    }

}
