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

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Adds the JobId to context for use by steps.
 *
 * @author Clifford Errickson
 */
@Component
public class JobIdToContextExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(final JobExecution jobExecution) {
        jobExecution.getExecutionContext().put("jobId", jobExecution.getJobId());
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        // do nothing
    }

}
