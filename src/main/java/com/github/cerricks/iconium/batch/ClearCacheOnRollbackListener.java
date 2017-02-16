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

import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Listens for failed write attempts (resulting in a rollback) and clears the
 * internal {@link Cache} configured by
 * {@link #setCacheNames(String[]) setCacheNames(String[])} from the provided
 * {@link CacheManager}.
 *
 * @author Clifford Errickson
 * @param <T> item type
 */
@Component
@StepScope
public class ClearCacheOnRollbackListener<T> implements ItemWriteListener<T> {

    private static final Logger logger = LoggerFactory.getLogger(ClearCacheOnRollbackListener.class);

    private CacheManager cacheManager;

    @Value("${rollback.cache.names}")
    private String[] cacheNames = {};

    public ClearCacheOnRollbackListener() {
    }

    @PostConstruct
    public void init() {
        Assert.notNull(cacheManager, "[Assertion failed] - CacheManager must not be null");
    }

    /**
     * Configure the {@link CacheManager} that contains the caches to clear on
     * write error.
     *
     * @param cacheManager the {@link CacheManager} that contains the caches to
     * clear on write error.
     */
    @Autowired
    public void setCacheManager(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Set the names of the caches to be cleared on write errors.
     *
     * @param cacheNames names of the caches to be cleared on write errors.
     */
    public void setCacheNames(final String[] cacheNames) {
        if (cacheNames != null) {
            this.cacheNames = Arrays.copyOf(cacheNames, cacheNames.length);
        }
    }

    @Override
    public void beforeWrite(final List<? extends T> items) {
        // do nothing
    }

    @Override
    public void afterWrite(final List<? extends T> items) {
        // do nothing
    }

    /**
     * Removes cached items following a failure to write items resulting in a
     * rollback.
     *
     * @param exception the exception that was thrown.
     * @param items the list of items that failed to write.
     */
    @Override
    public void onWriteError(final Exception exception, final List<? extends T> items) {
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);

            if (cache == null) {
                logger.warn("Cannot clear cache with name [" + cacheName + "]. Cache not found.");
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Clearing cache [" + cacheName + "] following error: " + exception.getMessage());
                }

                cache.clear();
            }
        }
    }

}
