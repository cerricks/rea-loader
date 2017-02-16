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

import com.github.cerricks.iconium.data.PropertyListing;
import com.github.cerricks.iconium.service.PropertyListingService;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Handles writing {@code PropertyListing} using a
 * {@link PropertyListingService}.
 *
 * @author Clifford Errickson
 */
@Component
@StepScope
public class PropertyListingWriter implements ItemWriter<PropertyListing> {

    private static final Logger logger = LoggerFactory.getLogger(PropertyListingWriter.class);

    private PropertyListingService propertyListingService;

    public PropertyListingWriter() {
    }

    @PostConstruct
    public void init() {
        Assert.notNull(propertyListingService, "[Assertion failed] - PropertyListingService must not be null");
    }

    /**
     * Configure the {@link PropertyListingService} used to process
     * {@code PropertyListing} objects.
     *
     * @param propertyListingService the {@link PropertyListingService} used to
     * process {@code PropertyListing} objects.
     */
    @Autowired
    public void setPropertyListingService(final PropertyListingService propertyListingService) {
        this.propertyListingService = propertyListingService;
    }

    @Override
    public void write(final List<? extends PropertyListing> items)
            throws Exception {
        for (PropertyListing listing : items) {
            propertyListingService.save(listing);
        }
    }

}
