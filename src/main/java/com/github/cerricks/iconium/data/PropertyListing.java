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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Contains details on a real estate property listing.
 *
 * @author Clifford Errickson
 */
public class PropertyListing {

    private String type;
    private String cachedPageId;
    private String url;
    private LocalDate crawlDate;
    private DateTime crawlDateTime;
    private String inputAddress;
    private final Property propertyDetails;

    public PropertyListing() {
        this.propertyDetails = new Property();
    }

    public Property getPropertyDetails() {
        return propertyDetails;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCachedPageId() {
        return cachedPageId;
    }

    public void setCachedPageId(String cachedPageId) {
        this.cachedPageId = cachedPageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getCrawlDate() {
        return crawlDate;
    }

    public void setCrawlDate(LocalDate crawlDate) {
        this.crawlDate = crawlDate;
    }

    public DateTime getCrawlDateTime() {
        return crawlDateTime;
    }

    public void setCrawlDateTime(DateTime crawlDateTime) {
        this.crawlDateTime = crawlDateTime;
    }

    public String getInputAddress() {
        return inputAddress;
    }

    public void setInputAddress(String inputAddress) {
        this.inputAddress = inputAddress;
    }

}
