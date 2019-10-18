#!/bin/bash

jarsigner -keystore /home/mbc/projects/PlateManager/webstart/keystore/JWSKeystore -storepass fjdksiop1 -keypass fjdksiop1 /home/mbc/projects/ln/target/uberjar/ln-0.1.0-SNAPSHOT-standalone.jar info@labsolns.com

##clicking on jnlp in local directory will not work.  
##jnlp will launch  whatever is installed on web site	

cp /home/mbc/projects/ln/target/uberjar/ln-0.1.0-SNAPSHOT-standalone.jar /home/mbc/syncd/labsolns/public/client

cp /home/mbc/projects/ln/target/uberjar/ln-0.1.0-SNAPSHOT-standalone.jar /home/mbc/syncd/labsolns/public/webstart

