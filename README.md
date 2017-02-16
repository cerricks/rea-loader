# rea-loader

A java utility for loading historical residential property listing data from a JSON file into a relational database.

## Prerequisites
1. Execute rea-setup.sql to create indexed tables addr_txt_to_id_tbl and street_locality_tbl

## Usage
```
java -jar target/rea-loader-1.0.jar input.file=input.json
```
