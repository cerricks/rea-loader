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

import org.joda.time.LocalDate;
import org.springframework.dao.DataAccessException;

/**
 * A data access object interface for {@link Property} objects.
 *
 * @author Clifford Errickson
 */
public interface PropertyDao {

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
    void addComparableProperty(Long propertyId, Long comparablePropertyId, String type, LocalDate date)
            throws DataAccessException;

    /**
     * Add data acquisition details for a given property.
     *
     * @param addressPID the AddressPID of the address in the GNAF database
     * @param url URL of the request for data
     * @param acquiredOn the date the data was acquired
     * @param propertyId the ID of the property for which details were extracted
     * @throws DataAccessException on data access error
     */
    void addDataAcquisition(String addressPID, String url, LocalDate acquiredOn, Long propertyId)
            throws DataAccessException;

    /**
     * Add property rental/sale history.
     *
     * @param propertyId The ID of the property to which the history applies.
     * @param event event details
     * @throws DataAccessException on data access error
     */
    void addEvent(Long propertyId, Event event)
            throws DataAccessException;

    /**
     * Add property details.
     *
     * @param property property details
     * @param recordDate the date for this record
     * @return auto-generated property ID
     * @throws DataAccessException on data access error
     */
    Long addProperty(Property property, LocalDate recordDate)
            throws DataAccessException;

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
    Long findPropertyIdByAddress(String address, String state, String postCode, String locality, LocalDate recordDate)
            throws DataAccessException;

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
    Long findPropertyIdByAddressPID(String addressPID, LocalDate recordDate)
            throws DataAccessException;

    /**
     * Update property details using {@link Property#getId()} to identify the
     * property record to update.
     *
     * @param property property details to update
     * @return the number of affected rows
     * @throws DataAccessException on data access error
     */
    int updateProperty(Property property)
            throws DataAccessException;

}
