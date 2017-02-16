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
package com.github.cerricks.iconium.service;

import com.github.cerricks.iconium.data.AddressDao;
import com.github.cerricks.iconium.data.Event;
import com.github.cerricks.iconium.data.Property;
import com.github.cerricks.iconium.data.PropertyDao;
import com.github.cerricks.iconium.data.PropertyListing;
import com.github.cerricks.iconium.data.School;
import com.github.cerricks.iconium.data.SchoolDao;
import java.util.List;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link PropertyListingService} for managing
 * {@link PropertyListin} objects in a relational database.
 *
 * @author Clifford Errickson
 */
@Service
public class PropertyListingServiceImpl implements PropertyListingService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyListingServiceImpl.class);

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private PropertyDao propertyDao;

    @Autowired
    private SchoolDao schoolDao;

    @Override
    public void save(final PropertyListing listing) {
        Property property = listing.getPropertyDetails();

        // lookup address PID if missing
        if (property.getAddressPID() == null) {
            property.setAddressPID(addressDao.findAddressDetailPID(property.getAddress(), property.getState(), property.getPostCode(), property.getLocality()));
        }

        // check for existing record for this property
        if (property.getAddressPID() != null) {
            property.setId(propertyDao.findPropertyIdByAddressPID(property.getAddressPID(), listing.getCrawlDate()));
        } else {
            property.setId(propertyDao.findPropertyIdByAddress(property.getAddress(), property.getState(), property.getPostCode(), property.getLocality(), listing.getCrawlDate()));
        }

        // update (existing) or add (new) property details
        if (property.getId() != null) {
            propertyDao.updateProperty(property);
        } else {
            Long propertyId = propertyDao.addProperty(property, listing.getCrawlDate());

            property.setId(propertyId);
        }

        // process comparable properties
        processComparableProperties(property.getId(), property.getComparablePropertiesForSale(), "for sale", listing.getCrawlDate());
        processComparableProperties(property.getId(), property.getComparablePropertiesForRent(), "for rent", listing.getCrawlDate());
        processComparableProperties(property.getId(), property.getComparablePropertiesSold(), "sold", listing.getCrawlDate());

        // process nearby schools
        for (School school : property.getNearbySchools()) {
            Long schoolId = schoolDao.findSchoolId(school.getName(), school.getType(), school.getSector());

            if (schoolId == null) {
                school.setStreetLocalityPID(addressDao.findStreetLocalityPID(school.getStreet(), school.getState(), school.getPostCode(), school.getLocality()));

                schoolId = schoolDao.addSchool(school);
            }

            if (schoolId != null) {
                try {
                    schoolDao.addSchoolDistance(property.getId(), schoolId, school.getDistance());
                } catch (DuplicateKeyException ex) {
                    logger.warn(ex.getMessage());
                }
            }
        }

        // process property events
        for (Event event : property.getHistory()) {
            try {
                propertyDao.addEvent(property.getId(), event);
            } catch (DuplicateKeyException ex) {
                logger.warn(ex.getMessage());
            }
        }

        // process data acquisition
        try {
            propertyDao.addDataAcquisition(property.getAddressPID(), listing.getUrl(), listing.getCrawlDate(), property.getId());
        } catch (DuplicateKeyException ex) {
            logger.warn(ex.getMessage());
        }
    }

    private void processComparableProperties(final Long propertyId, final List<Property> comparableProperties, final String comparisonType, final LocalDate comparisonDate)
            throws DataAccessException {
        for (Property comparableProperty : comparableProperties) {
            comparableProperty.setAddressPID(addressDao.findAddressDetailPID(comparableProperty.getAddress(), comparableProperty.getState(), comparableProperty.getPostCode(), comparableProperty.getLocality()));

            // check for existing record for this property
            if (comparableProperty.getAddressPID() != null) {
                comparableProperty.setId(propertyDao.findPropertyIdByAddressPID(comparableProperty.getAddressPID(), comparisonDate));
            } else {
                comparableProperty.setId(propertyDao.findPropertyIdByAddress(comparableProperty.getAddress(), comparableProperty.getState(), comparableProperty.getPostCode(), comparableProperty.getLocality(), comparisonDate));
            }

            // add comparable property details if necessary
            if (comparableProperty.getId() == null) {
                comparableProperty.setId(propertyDao.addProperty(comparableProperty, comparisonDate));
            }

            // associate comparable property with listing property
            try {
                propertyDao.addComparableProperty(propertyId, comparableProperty.getId(), comparisonType, comparisonDate);
            } catch (DuplicateKeyException ex) {
                logger.warn(ex.getMessage());
            }
        }
    }

}
