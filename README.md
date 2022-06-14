# spark_converter

Utility to convert from one Spark supported format to another. 

Currently it is possible to move from JSON to Parquet and vice-versa. 

## Configuration 

See the `reference.conf` file for an example configuration. Each entry in `inputs` must have a type of either 'json-2-parquet' or 'parquet-2-json'. The input file will be read in and then written out in the desired format. 

If you are running Spark locally the `spark.uri` field should be set to 'local[*]'. 

## Building executable

To create a Jar file run `sbt assembly` which will create a jar under `/target/scala-2.12/`. 

## Running the jar

The jar can be submitted to a Dataproc cluster, or run locally. When run locally pass in a completed configuration file:

```
java -Xms1G -Xmx6G -Xss1M -XX:+CMSClassUnloadingEnabled \
    -Dconfig.file=<configuration file> \
    -jar <jar_file>
```
