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

import org.springframework.dao.DataAccessException;

/**
 * A data access object interface for retrieving address information.
 *
 * @author Clifford Errickson
 */
public interface AddressDao {

    /**
     * Retrieve the AddressDetailsPID value for the matching address. This
     * method will return {@code null} if a match is not made.
     *
     * @param address address value
     * @param state state value
     * @param postCode post code value
     * @param locality locality value
     * @return the AddressDetailsPID matching the given address or {@code null}
     * if no match is found.
     * @throws DataAccessException on data access error
     */
    public String findAddressDetailPID(String address, String state, String postCode, String locality)
            throws DataAccessException;

    /**
     * Retrieve the StreetLocalityPID value for the matching street address.
     * This method will return {@code null} if a match is not made.
     *
     * @param street street value
     * @param state state value
     * @param postCode post code value
     * @param locality locality value
     * @return the StreetLocalityPID matching the given street address or
     * {@code null} if no match is found.
     * @throws DataAccessException on data access error
     */
    String findStreetLocalityPID(String street, String state, String postCode, String locality)
            throws DataAccessException;

}
