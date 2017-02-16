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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * A data access object JDBC implementation for retrieving address information
 * from the GNAF database.
 *
 * @author Clifford Errickson
 */
@Repository
public class AddressJdbcDao implements AddressDao {

    private static final Logger logger = LoggerFactory.getLogger(AddressJdbcDao.class);

    /**
     * SQL: Select address details PID for given address.
     */
    private static final String SELECT_ADDRESS_DETAIL_PID
            = "SELECT "
            + "  address_detail_pid "
            + "FROM "
            + "  gnaf.addr_txt_to_id_v "
            + "WHERE "
            + "  address LIKE UPPER(?) "
            + "AND state = ? "
            + "AND (post_code IS NULL OR post_code = ?) "
            + "AND locality = UPPER(?)";

    /**
     * SQL: Select street locality PID for given locality.
     */
    private static final String SELECT_STREET_LOCALITY_PID
            = "SELECT "
            + "  street_locality_pid "
            + "FROM "
            + "  gnaf.street_locality_v "
            + "WHERE "
            + "  state = ? "
            + "AND (post_code IS NULL OR post_code = ?) "
            + "AND locality = ? "
            + "AND street_desc = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates instance of an {@code GnafAddressJdbcDao}.
     *
     * @param jdbcTemplate the {@link JdbcTemplate} to use for data access.
     */
    @Autowired
    public AddressJdbcDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = (jdbcTemplate);
    }

    /**
     * Retrieve the AddressDetailsPID value for the matching address. This
     * method will return {@code null} if a match is not made.
     *
     * <p>
     * By default, this method will cache results to improve performance.
     *
     * @param address address value
     * @param state state value
     * @param postCode post code value
     * @param locality locality value
     * @return the AddressDetailsPID matching the given address or {@code null}
     * if no match is found.
     * @throws DataAccessException on data access error
     */
    @Cacheable(value = "gnaf_address_pid_cache")
    @Override
    public String findAddressDetailPID(final String address, final String state, final String postCode, final String locality)
            throws DataAccessException {
        Object[] parameters = new Object[]{
            address + '%', // use wildcard to match the first part of the address
            state,
            postCode,
            locality
        };

        try {
            return jdbcTemplate.queryForObject(SELECT_ADDRESS_DETAIL_PID, parameters, String.class);
        } catch (EmptyResultDataAccessException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to match address in GNAF database for: address [" + address + "], state [" + state + "], postCode [" + postCode + "], and locality [" + locality + "]");
            }

            return null;
        }
    }

    /**
     * Retrieve the StreetLocalityPID value for the matching street address.
     * This method will return {@code null} if a match is not made.
     *
     * <p>
     * By default, this method will cache results to improve performance.
     *
     * @param street street value
     * @param state state value
     * @param postCode post code value
     * @param locality locality value
     * @return the StreetLocalityPID matching the given street address or
     * {@code null} if no match is found.
     * @throws DataAccessException on data access error
     */
    @Cacheable(value = "gnaf_street_locality_pid_cache")
    @Override
    public String findStreetLocalityPID(final String street, final String state, final String postCode, final String locality)
            throws DataAccessException {
        Object[] parameters = new Object[]{
            state,
            postCode,
            locality,
            street
        };

        try {
            return jdbcTemplate.queryForObject(SELECT_STREET_LOCALITY_PID, parameters, String.class);
        } catch (EmptyResultDataAccessException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to match street locality in GNAF database for: street [" + street + "], state [" + state + "], postCode [" + postCode + "], and locality [" + locality + "]");
            }

            return null;
        }
    }

}
