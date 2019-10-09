#!/bin/bash

javadoc -d ./javadocs ../src/java/*.java

## f so not prompted for each file
rm -rf /home/mbc/syncd/labsolns/javadocs

cp -r ./javadocs /home/mbc/syncd/labsolns/public/
