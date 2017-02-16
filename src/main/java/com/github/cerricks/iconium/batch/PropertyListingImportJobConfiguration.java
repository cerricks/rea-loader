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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.github.cerricks.iconium.data.PropertyListing;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the batch job to load {@link PropertyListing} objects.
 *
 * @author Clifford Errickson
 */
@Configuration
@EnableBatchProcessing
@EnableCaching
public class PropertyListingImportJobConfiguration {

    @Value("${batch.skip.limit}")
    int skipLimit = 5000;

    @Value("${batch.commit.interval}")
    int commitLimit = 2500;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ClearCacheOnRollbackListener clearCacheOnRollbackListener;

    @Autowired
    private JobStatusNotificationListener jobStatusCompletionListener;

    @Autowired
    private JobIdToContextExecutionListener jobIdToContextExecutionListener;

    @Autowired
    private JsonFileLoggerSkipListener jsonFileLoggerSkipListener;

    @Autowired
    private JsonNodeReader jsonNodeReader;

    @Autowired
    private JsonPropertyListingProcessor jsonPropertyListingProcessor;

    @Autowired
    private PropertyListingWriter propertyListingWriter;

    @Bean
    public JsonFactory getJsonFactory() {
        return new MappingJsonFactory();
    }

    @Bean
    public Job importPropertyListingJob() {
        return jobBuilderFactory.get("propertyListingImportJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobStatusCompletionListener)
                .listener(jobIdToContextExecutionListener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("load")
                .<JsonNode, JsonPropertyListing>chunk(commitLimit)
                .faultTolerant().listener(jsonFileLoggerSkipListener).skip(Exception.class).skipLimit(skipLimit)
                .reader(jsonNodeReader)
                .processor(jsonPropertyListingProcessor)
                .writer(propertyListingWriter)
                .listener(clearCacheOnRollbackListener)
                .build();
    }

}
