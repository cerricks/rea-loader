#rea-loader

##Summary

rea-loader is a utility used to load historical real estate property data parsed from a JSON file into a relational database. The utility, written in Java, is built using the [Spring Batch framework](http://projects.spring.io/spring-batch/).

##Directory Layout

The project is structured as follows:

Path | Description
---- | -----------
config | Application configuration files
logs | Application output files (generated on run)
src/main | Application sources
src/main/resources | Application resources
target | Contains compiled source (generated on build)

##Configuration

Various settings may be configured externally my modifying parameters in `config/application.properties`.

Parameter Name | Description
-------------- | -----------
spring.datasource.url | The URL of the database
spring.datasource.username | The ID of the database user
spring.datasource.password | The password of the database user
batch.commit.interval | The number of records to process before issuing a database COMMIT
batch.skip.limit | The number of items allowed to skip/fail before job is FAILED

##Usage

```
java -jar target/rea-loader-1.1.jar input.file=input.json
```

Where `input.file` is the path to the input file containing JSON content.

##Logging

During execution, log statements will be written to file `logs/rea-loader.log`. The default level for logged statements is `WARN`. Modify `config/logback.xml` for greater control over logging.

##Caching

Internal caching is used to improve performance. In total, 5 caches are used:

Cache Name | Description
---------- | -----------
gnaf_address_pid_cache | Stores address details PID values retrieved from the GNAF database
gnaf_street_locality_pid_cache | Stores street locality PID values retrieved from the GNAF database
property_id_by_address_cache | Stores property ID values by address
property_id_by_address_pid_cache | Stores property ID values by address details PID
school_id_cache | Stores school ID values by name

Modify `config/ehcache.xml` for greater control over caching.

##Skipped Items

An item will be skipped following an error parsing JSON content into property details OR saving the resulting property details in the database.

In the event of a skipped item, the original JSON content for that item will be output to `logs/skiplog-<jobId>.json`. In addition, details on the error will be logged to `logs/rea-loader.log`.

Configure property `batch.skip.limit` to set the number of skipped items allowed before the job fails.
##Batch Metadata

Information pertaining to the job execution and status is recorded in the database during runtime.

The following (9) objects will be created on job execution:

1. BATCH_JOB_EXECUTION
2. BATCH_JOB_EXECUTION_CONTEXT
3. BATCH_JOB_EXECUTION_PARAMS
4. BATCH_JOB_EXECUTION_SEQ
5. BATCH_JOB_INSTANCE
6. BATCH_JOB_SEQ
7. BATCH_STEP_EXECUTION
8. BATCH_STEP_EXECUTION_CONTEXT
9. BATCH_STEP_EXECUTION_SEQ

Additional information on Spring Batch Meta-Data objects can be found [here](http://docs.spring.io/spring-batch/reference/html/metaDataSchema.html).
