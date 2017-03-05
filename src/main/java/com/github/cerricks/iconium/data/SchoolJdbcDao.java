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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
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

/**
 * A data access object JDBC implementation for {@link School} objects.
 *
 * @author Clifford Errickson
 */
@Repository
public class SchoolJdbcDao implements SchoolDao {

    private static final Logger logger = LoggerFactory.getLogger(SchoolJdbcDao.class);

    /**
     * SQL: Insert school details.
     */
    private static final String INSERT_SCHOOL
            = "INSERT "
            + "INTO "
            + "  rea.schools "
            + "  ( "
            + "	   name, "
            + "	   website, "
            + "	   type, "
            + "	   sector, "
            + "	   gnaf_street_locality_pid "
            + "  ) "
            + "VALUES "
            + "  (?, ?, ?, ?, ?)";

    /**
     * SQL: Insert school distance relative to given property.
     */
    private static final String INSERT_SCHOOL_DISTANCE
            = "INSERT INTO "
            + "  rea.schools_near_props "
            + "  ( "
            + "	   prop_dtls_id, "
            + "	   school_id, "
            + "	   distance_desc "
            + "  ) "
            + "VALUES "
            + "  (?, ?, ?)";

    /**
     * SQL: Select school id matching given criteria.
     */
    private static final String SELECT_SCHOOL_ID
            = "SELECT "
            + "  school_id "
            + "FROM "
            + "  rea.schools "
            + "WHERE "
            + "  name = ? "
            + "AND type = ? "
            + "AND sector = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates instance of a {@code SchoolJdbcDao}.
     *
     * @param jdbcTemplate the {@link JdbcTemplate} to use for data access.
     */
    @Autowired
    public SchoolJdbcDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Add school details.
     *
     * @param school school details
     * @return auto-generated school id number
     * @throws DataAccessException on data access error
     */
    @CachePut(value = "school_id_cache", key = "{#school.name, #school.type, #school.sector}", unless = "#result == null")
    @Override
    public Long addSchool(final School school)
            throws DataAccessException {
        Assert.notNull(school);

        final Object[] parameters = new Object[]{
            school.getName(),
            school.getWebsite(),
            school.getType(),
            school.getSector(),
            school.getStreetLocalityPID()
        };

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_SCHOOL, Statement.RETURN_GENERATED_KEYS);

                new ArgumentPreparedStatementSetter(parameters).setValues(ps);

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    /**
     * Add school distance relative to given property.
     *
     * @param propertyId property internal id number
     * @param schoolId school internal id number
     * @param distance relative distance
     * @throws DataAccessException on data access error
     */
    @Override
    public void addSchoolDistance(final Long propertyId, final Long schoolId, final String distance)
            throws DataAccessException {
        Assert.notNull(propertyId);
        Assert.notNull(schoolId);

        Object[] parameters = new Object[]{
            propertyId,
            schoolId,
            distance
        };

        jdbcTemplate.update(INSERT_SCHOOL_DISTANCE, parameters);
    }

    /**
     * Retrieve the id number of an existing school matching the given criteria.
     *
     * @param name school name
     * @param type school type
     * @param sector school sector
     * @return the id number of the school OR {@code NULL} if not found.
     * @throws DataAccessException on data access error
     */
    @Cacheable(value = "school_id_cache", key = "{#name, #type, #sector}", unless = "#result == null")
    @Override
    public Long findSchoolId(final String name, final String type, final String sector)
            throws DataAccessException {
        Object[] parameters = new Object[]{
            name,
            type,
            sector
        };

        try {
            return jdbcTemplate.queryForObject(SELECT_SCHOOL_ID, parameters, Long.class);
        } catch (EmptyResultDataAccessException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to match school for: name [" + name + "], type [" + type + "], and sector [" + sector + "]");
            }

            return null;
        }
    }

}
