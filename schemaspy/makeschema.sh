#!/bin/bash

java -jar ./schemaspy-6.1.0-SNAPSHOT.jar

## f so not prompted for each file
rm -rf /home/mbc/syncd/labsolns/public/schema

cp -r ./schema /home/mbc/syncd/labsolns/public/
