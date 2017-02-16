/*
 * Copyright 2017 Pivotal Software, Inc..
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A data access object JDBC implementation for {@link Property} objects.
 *
 * @author Clifford Errickson
 */
@Repository
public class PropertyJdbcDao implements PropertyDao {

    private static final Logger logger = LoggerFactory.getLogger(PropertyJdbcDao.class);

    /**
     * SQL: Insert comparable property associated with a property.
     */
    private static final String INSERT_COMPARABLE_PROPERTY
            = "INSERT "
            + "INTO "
            + "  rea.comparable_properties "
            + "  ( "
            + "    prop_compared_id, "
            + "    comparable_prop_id, "
            + "    comparison_type, "
            + "    compared_on "
            + "  ) "
            + "  VALUES "
            + "  (?, ?, ?, ?)";

    /**
     * SQL: Insert data acquisition details.
     */
    public static final String INSERT_DATA_ACQUISITION
            = "INSERT "
            + "INTO "
            + "  rea.data_acquisition "
            + "  ( "
            + "    gnaf_addr_dtl_pid, "
            + "    url, "
            + "    acquired_on, "
            + "    prop_dtls_id "
            + "  ) "
            + "  VALUES "
            + "  (?, ?, ?, ?)";

    /**
     * SQL: Insert property history.
     */
    private static final String INSERT_EVENT
            = "INSERT "
            + "INTO "
            + "  rea.property_sale_rent_hist "
            + "  ( "
            + "    prop_dtls_id, "
            + "    event_year, "
            + "    event_month, "
            + "    event_type, "
            + "    price_desc "
            + "  ) "
            + "  VALUES "
            + "  (?, ?, ?, ?, ?)";

    /**
     * SQL: Insert property details.
     */
    private static final String INSERT_PROPERTY
            = "INSERT "
            + "INTO "
            + "  rea.property_details "
            + "  ( "
            + "    gnaf_addr_dtl_pid, "
            + "    as_at, "
            + "    state, "
            + "    post_code, "
            + "    locality, "
            + "    address, "
            + "    property_type, "
            + "    bedrooms, "
            + "    bathrooms, "
            + "    car_spots, "
            + "    land_size_desc, "
            + "    bldg_size_desc, "
            + "    council_area, "
            + "    price_desc, "
            + "    price_estimate_from, "
            + "    price_estimate_to, "
            + "    price_estimate_confidence, "
            + "    sale_method, "
            + "    sold_date, "
            + "    avail_for_lease, "
            + "    year_built, "
            + "    block_code "
            + "  ) "
            + "  VALUES "
            + "  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * SQL: Select property ID for given address.
     */
    private static final String SELECT_PROPERTY_BY_ADDRESS
            = "SELECT "
            + "  prop_dtls_id "
            + "FROM "
            + "  rea.property_details "
            + "WHERE "
            + "  address = ? "
            + "AND state = ? "
            + "AND (post_code IS NULL OR post_code = ?) "
            + "AND locality = ? "
            + "AND as_at = ?";

    /**
     * SQL: Select property ID for given addressPID.
     */
    private static final String SELECT_PROPERTY_BY_ADDRESS_PID
            = "SELECT "
            + "  prop_dtls_id "
            + "FROM "
            + "  rea.property_details "
            + "WHERE "
            + "  gnaf_addr_dtl_pid = ? "
            + "AND as_at           = ?";

    /**
     * Update property details.
     */
    private static final String UPDATE_PROPERTY
            = "UPDATE "
            + "  rea.property_details "
            + "SET "
            + "  property_type             = ?, "
            + "  bedrooms                  = ?, "
            + "  bathrooms                 = ?, "
            + "  car_spots                 = ?, "
            + "  land_size_desc            = ?, "
            + "  bldg_size_desc            = ?, "
            + "  council_area              = ?, "
            + "  price_estimate_from       = ?, "
            + "  price_estimate_to         = ?, "
            + "  price_estimate_confidence = ?, "
            + "  year_built                = ?, "
            + "  block_code                = ? "
            + "WHERE "
            + "  prop_dtls_id = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates instance of a {@code PropertyJdbcDao}.
     *
     * @param jdbcTemplate the {@link JdbcTemplate} to use for data access.
     */
    @Autowired
    public PropertyJdbcDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Add comparable property associated with given property.
     *
     * @param propertyId The ID of the property for which the comparable
     * property has been found.
     * @param comparablePropertyId The ID of a property considered comparable.
     * @param type The type of comparison e.g. for rent, for sale, sold.
     * @param date The date the comparison was made.
     * @throws DataAccessException on data access error
     */
    @Override
    public void addComparableProperty(final Long propertyId, final Long comparablePropertyId, final String type, final LocalDate date)
            throws DataAccessException {
        Assert.notNull(propertyId);
        Assert.notNull(comparablePropertyId);
        Assert.notNull(type);
        Assert.notNull(date);

        Object[] parameters = new Object[]{
            propertyId,
            comparablePropertyId,
            type,
            date.toDate()
        };

        jdbcTemplate.update(INSERT_COMPARABLE_PROPERTY, parameters);
    }

    /**
     * Add data acquisition details for a given property.
     *
     * @param addressPID the AddressPID of the address in the GNAF database
     * @param url URL of the request for data
     * @param acquiredOn the date the data was acquired
     * @param propertyId the ID of the property for which details were extracted
     * @throws DataAccessException on data access error
     */
    @Override
    public void addDataAcquisition(final String addressPID, final String url, final LocalDate acquiredOn, final Long propertyId)
            throws DataAccessException {
        Assert.notNull(addressPID);
        Assert.notNull(url);
        Assert.notNull(acquiredOn);
        Assert.notNull(propertyId);

        Object[] parameters = new Object[]{
            addressPID,
            url,
            acquiredOn.toDate(),
            propertyId
        };

        jdbcTemplate.update(INSERT_DATA_ACQUISITION, parameters);
    }

    /**
     * Add property rental/sale history.
     *
     * @param propertyId The ID of the property to which the history applies.
     * @param event event details
     * @throws DataAccessException on data access error
     */
    @Override
    public void addEvent(final Long propertyId, final Event event)
            throws DataAccessException {
        Assert.notNull(event);
        Assert.notNull(event.getYearMonth());
        Assert.notNull(event.getType());

        Object[] parameters = new Object[]{
            propertyId,
            event.getYear(),
            event.getMonth(),
            convertEventTypeText(event.getType()),
            event.getPriceDesc()
        };

        jdbcTemplate.update(INSERT_EVENT, parameters);
    }

    /**
     * Add property details.
     *
     * @param property property details
     * @param recordDate the date for this record
     * @return auto-generated property ID
     * @throws DataAccessException on data access error
     */
    @Override
    public Long addProperty(final Property property, final LocalDate recordDate)
            throws DataAccessException {
        Assert.notNull(property);
        Assert.notNull(recordDate);

        final Object[] parameters = new Object[]{
            property.getAddressPID(),
            recordDate.toDate(),
            property.getAddressPID() == null ? property.getState() : null,
            property.getAddressPID() == null ? property.getPostCode() : null,
            property.getAddressPID() == null ? property.getLocality() : null,
            property.getAddressPID() == null ? property.getAddress() : null,
            property.getPropertyType(),
            property.getBedrooms(),
            property.getBathrooms(),
            property.getCarSpots(),
            property.getLandSizeDesc(),
            property.getBuildingSizeDesc(),
            property.getCouncilArea(),
            property.getPriceDesc(),
            property.getPriceEstimateFrom(),
            property.getPriceEstimateTo(),
            property.getPriceEstimateConfidence(),
            property.getSaleMethod(),
            property.getSoldDate() != null ? property.getSoldDate().toDate() : null,
            property.getAvailableForLeaseDate() != null ? property.getAvailableForLeaseDate().toDate() : null,
            property.getYearBuilt(),
            property.getBlockCode()
        };

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_PROPERTY, Statement.RETURN_GENERATED_KEYS);

                new ArgumentPreparedStatementSetter(parameters).setValues(ps);

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    /**
     * Retrieve property id matching given address details. This method will
     * return {@code null} if a match is not made.
     *
     * @param address address value
     * @param state state value
     * @param postCode post code value
     * @param locality locality value
     * @param recordDate the date for this record
     * @return ID of property matching given address or {@code null} if no match
     * found.
     * @throws DataAccessException on data access error
     */
    @Cacheable(value = "property_id_by_address_cache", unless = "#result == null")
    @Override
    public Long findPropertyIdByAddress(final String address, final String state, final String postCode, final String locality, final LocalDate recordDate)
            throws DataAccessException {
        if (!StringUtils.hasText(address)
                && !StringUtils.hasText(state)
                && !StringUtils.hasText(postCode)
                && !StringUtils.hasText(locality)) {
            if (logger.isInfoEnabled()) {
                logger.info("");
            }

            return null;
        }

        if (recordDate == null) {
            if (logger.isInfoEnabled()) {
                logger.info("recordDate cannot be NULL");
            }

            return null;
        }

        Object[] parameters = new Object[]{
            address,
            state,
            postCode,
            locality,
            recordDate.toDate()
        };

        try {
            return jdbcTemplate.queryForObject(SELECT_PROPERTY_BY_ADDRESS, parameters, Long.class);
        } catch (EmptyResultDataAccessException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to match property for: address [" + address + "], state [" + state + "], postCode [" + postCode + "], locality [" + locality + "] and recordDate [" + recordDate.toString() + "]");
            }

            return null;
        }
    }

    /**
     * Retrieve property id matching given addressPID. This method will return
     * {@code null} if a match is not made.
     *
     * @param addressPID the AddressPID of the address in the GNAF database
     * @param recordDate the date for this record
     * @return ID of property matching given addressPID or {@code null} if no
     * match found.
     * @throws DataAccessException on data access error
     */
    @Cacheable(value = "property_id_by_address_pid_cache", unless = "#result == null")
    @Override
    public Long findPropertyIdByAddressPID(final String addressPID, final LocalDate recordDate)
            throws DataAccessException {
        if (addressPID == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("AddressPID details cannot be NULL");
            }

            return null;
        }

        Assert.notNull(recordDate);

        Object[] parameters = new Object[]{
            addressPID,
            recordDate.toDate()
        };

        try {
            return jdbcTemplate.queryForObject(SELECT_PROPERTY_BY_ADDRESS_PID, parameters, Long.class);
        } catch (EmptyResultDataAccessException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to match property for: addressPID [" + addressPID + "] and recordDate [" + recordDate.toString() + "]");
            }

            return null;
        }
    }

    /**
     * Update property details using {@link Property#getId()} to identify the
     * property record to update.
     *
     * @param property property details to update
     * @return the number of affected rows
     * @throws DataAccessException on data access error
     */
    @Override
    public int updateProperty(final Property property)
            throws DataAccessException {
        Assert.notNull(property);
        Assert.notNull(property.getId());

        final Object[] parameters = new Object[]{
            property.getPropertyType(),
            property.getBedrooms(),
            property.getBathrooms(),
            property.getCarSpots(),
            property.getLandSizeDesc(),
            property.getBuildingSizeDesc(),
            property.getCouncilArea(),
            property.getPriceEstimateFrom(),
            property.getPriceEstimateTo(),
            property.getPriceEstimateConfidence(),
            property.getYearBuilt(),
            property.getBlockCode(),
            property.getId()
        };

        return jdbcTemplate.update(UPDATE_PROPERTY, parameters);
    }

    /**
     * Try to convert event type text to either 'rented' or 'sold'. Returns
     * given text if match not found.
     *
     * @param typeText type text to convert
     * @return either 'rented', 'sold', or original value (if unable to convert)
     */
    private String convertEventTypeText(final String typeText) {
        if (typeText == null) {
            return null;
        }

        switch (typeText) {
            case "rent":
            case "rentalCampaign":
                return "rented";

            case "sold":
                return "sold";

            default:
                logger.warn("Unexpected event type [" + typeText + "]. Unable to convert to 'rented' or 'sold'.");

                return typeText;
        }
    }

}
