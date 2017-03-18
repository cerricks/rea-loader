/*
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Restartable {@link ItemReader} that reads {@link JsonNode} items from an
 * array of JSON objects from {@link #setResource(Resource)}.
 *
 * @author Clifford Errickson
 */
@Component
@StepScope
public class JsonNodeReader extends AbstractItemCountingItemStreamItemReader<JsonNode>
        implements ResourceAwareItemReaderItemStream<JsonNode> {

    private static final Logger logger = LoggerFactory.getLogger(JsonNodeReader.class);

    @Value("file:#{jobParameters['input.file']}")
    private Resource resource;

    private JsonFactory jsonFactory;
    private JsonParser parser;
    private boolean noInput = false;

    public JsonNodeReader() {
        super.setName(ClassUtils.getShortName(JsonNodeReader.class));
    }

    @PostConstruct
    public void init() {
        Assert.notNull(resource, "[Assertion failed] - Resource must not be null");
        Assert.notNull(jsonFactory, "[Assertion failed] - JsonFactory must not be null");
    }

    /**
     * Set the {@link JsonFactory} used to construct the {@code JsonParser}
     * needed to parse the {@link #setResource(Resource)}.
     *
     * @param jsonFactory used to construct the {@code JsonParser} needed to
     * parse the {@link #setResource(Resource)}
     */
    @Autowired
    public void setJsonFactory(final JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    /**
     * Set the resource to read input from.
     *
     * @param resource the resource to read input from.
     */
    @Override
    public void setResource(final Resource resource) {
        this.resource = resource;
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.notNull(resource, "Input resource must be set");

        noInput = true;

        if (!resource.exists()) {
            throw new IllegalStateException("Input resource must exist: " + resource);
        }

        if (!resource.isReadable()) {
            throw new IllegalStateException("Input resource must be readable: " + resource);
        }

        this.parser = jsonFactory.createParser(resource.getFile());

        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new JsonParseException(parser, "Expected array of objects");
        }

        noInput = false;
    }

    @Override
    protected void doClose() throws Exception {
        if (parser != null) {
            parser.close();
        }
    }

    @Override
    protected JsonNode doRead() throws Exception {
        if (noInput) {
            return null;
        }

        if (parser == null
                || parser.isClosed()) {
            throw new ReaderNotOpenException("Reader must be open before it can be read.");
        }

        JsonToken token = null;

        try {
            token = parser.nextToken();
        } catch (JsonEOFException ex) {
            logger.warn(ex.getMessage());

            token = null;
        }

        if (token == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No more elements to read from file");
            }

            try {
                parser.close();
            } catch (IOException ex) {
                logger.warn("Failed to close parser", ex);
            }

            return null;
        }

        if (token != JsonToken.START_OBJECT) {
            throw new JsonParseException(parser, "Unexpected token [" + token + "]", parser.getCurrentLocation());
        }

        return parser.readValueAsTree();
    }

}
