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

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Listens for skipped {@link JsonNode} and {@link JsonItem} items and writes
 * JSON content to file.
 *
 * @author Clifford Errickson
 */
@Component
@StepScope
public class JsonFileLoggerSkipListener implements SkipListener<JsonNode, JsonItem> {

    private static final Logger logger = LoggerFactory.getLogger(JsonFileLoggerSkipListener.class);

    @Value("file:${skip.file}")
    private Resource resource;

    private JsonFactory jsonFactory;
    private JsonGenerator jsonGenerator;

    public JsonFileLoggerSkipListener() {
    }

    @PostConstruct
    public void init() throws Exception {
        Assert.notNull(resource, "[Assertion failed] - Resource must not be null");

        jsonGenerator = jsonFactory.createGenerator(resource.getFile(), JsonEncoding.UTF8);
        jsonGenerator.enable(Feature.AUTO_CLOSE_TARGET);
        jsonGenerator.useDefaultPrettyPrinter();
        jsonGenerator.writeStartArray();
        jsonGenerator.setCodec(new ObjectMapper());
    }

    @PreDestroy
    public void destroy() {
        try {
            jsonGenerator.close();
        } catch (IOException ex) {
            logger.warn(ex.getMessage());
        }
    }

    /**
     * Set the {@link JsonFactory} used to construct the {@code JsonParser}
     * needed to write to the {@link #setResource(Resource)}.
     *
     * @param jsonFactory used to construct the {@code JsonParser} needed to
     * write to the {@link #setResource(Resource)}
     */
    @Autowired
    public void setJsonFactory(final JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    /**
     * Set the resource used to output skipped item details to.
     *
     * @param resource the resource used to output skipped item details to.
     */
    public void setResource(final Resource resource) {
        this.resource = resource;
    }

    @Override
    public void onSkipInRead(final Throwable t) {
        logger.error("Item skipped following error during [READ]", t);
    }

    @Override
    public void onSkipInProcess(final JsonNode item, final Throwable t) {
        logger.error("Item skipped following error during [PROCESS]", t);

        try {
            jsonGenerator.writeTree(item);
        } catch (IOException ex) {
            logger.warn("Failed to log skipped item details: " + ex.getMessage());
        }
    }

    @Override
    public void onSkipInWrite(final JsonItem item, final Throwable t) {
        logger.error("Item skipped following error during [WRITE]", t);

        try {
            jsonGenerator.writeTree(item.getJsonNode());
        } catch (IOException ex) {
            logger.warn("Failed to log skipped item details: " + ex.getMessage());
        }
    }

}
