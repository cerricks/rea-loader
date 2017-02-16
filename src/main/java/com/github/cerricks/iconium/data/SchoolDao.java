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
 * A data access object interface on {@link School} objects.
 *
 * @author Clifford Errickson
 */
public interface SchoolDao {

    /**
     * Add school details.
     *
     * @param school school details
     * @return auto-generated school id number
     * @throws DataAccessException on data access error
     */
    Long addSchool(School school)
            throws DataAccessException;

    /**
     * Add school distance relative to given property.
     *
     * @param propertyId property internal id number
     * @param schoolId school internal id number
     * @param distance relative distance
     * @throws DataAccessException on data access error
     */
    void addSchoolDistance(Long propertyId, Long schoolId, String distance)
            throws DataAccessException;

    /**
     * Retrieve the id number of an existing school matching the given criteria.
     *
     * @param name school name
     * @param type school type
     * @param sector school sector
     * @return the id number of the school OR {@code NULL} if not found.
     * @throws DataAccessException on data access error
     */
    Long findSchoolId(String name, String type, String sector)
            throws DataAccessException;

}
