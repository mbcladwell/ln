---
title: Local Properties File
categories:
  - Overview
tags:
  - installation
  - properties
  - developer
  - download
  - limsnucleus.properties
comments: true
---


LIMS\*Nucleus reads a local properties file containing connection information. Upon startup the client will look for the properties file (limsnucleus.properties) in the working directory i.e. the directory in which LIMS*Nucleus was started. 

LIMS\*Nucleus determines the working directory by looking at the following locations in order:

1. The directory in which the jar file was launched
2. The value of <code>java.lang.System.user.home</code>
3. The value of <code>java.lang.System.user.dir</code>
  


If not found, the LIMS\*Nucleus client will use hard coded connection and user data to connect to the example cloud database at ElephantSQL.com.  When you are ready to create your own instance of the Postgres database, a limsnucleus.properties file must be provided to redirect the client.

IMPORTANT!: To prevent auto-connection to the test cloud database, a valid limsnucleus.properties file must be in the working directory. All data added to the cloud instance is deleted and refreshed nightly.

Navigate to the directory containing the jar file and launch.

```text
 user:~/temp/limsnucleus$ java -jar ln-0.1.jar

```

The properties file looks like:

```text
sslmode=false
init=false
dbname=lndb
port=5432
host=192.168.1.11
source=internal
connuser=ln_admin
connpassword=welcome
user=ln_admin
password=welcome

```

## Properties file variables


|Variable |Notes|
|--|--|
|init|If true, launch into [database utilities](/software/dbutilities/) to initialize the database|
|source| local, internal, test, elephantsql or heroku |
|host|The IP of the postgres database|
|dbname|Name of the database|
|port|always 5432 for Postgres|
|help-url-prefix|URL of the help system|
|connuser|Postgres database user name for jdbc connection string|
|connpassword|Postgres database password for jdbc connection string|
|sslmode| required for heroku otherwise ignored|
|user|LIMS\*Nucleus user name for automatic login; use null to prompt for username and password|
|password|LIMS\*Nucleus  password associated with user; ignored if user is null|

Note the difference between user and connuser:
connuser and connpassword are used in the jdbc connection string. Provided for use with cloud services.
user and password are users added to the LIMS\*Nucleus database e.g. ln_admin or ln_user provided upon setup.
On a local installation, user/connuser and password/connpassword may be the same.

## Properties file examples

### Elephant SQL
```text
source=elephantsql
dbname=klohmim
host=raja.db.elephantsql.com
port=5432
user=klohmim
password=hwc3v4_rbkT-1EL-JBaqFq0thCXM
sslmode=none
help-url-prefix=http://labsolns.com/software/
user=ln_admin
password=welcome

```

### Heroku
```text

source=heroku
dbname=d6dmuea0hch
host=ec3-50-19-114-27.compute-1.amazonaws.com
port=5432
connuser=dpstpjslqch
connpassword=c5644d221fa636d8d8065d3366df0c6b78e7a5390453c4a18c9b20b2
sslmode=require
help-url-prefix=http://labsolns.com/software/
user=ln_admin
password=welcome
         

```

### Local database

```text
init=false
source=internal
dbtype=postgres
dbname=lndb
host=192.168.1.11
port=5432
user=ln_admin
password=welcome
sslmode=false
help-url-prefix=http://labsolns.com/software/ 
user=ln_admin
password=welcome

```


Test that the properties are correct by trying to connect to the database using psql:

Networked database:

```text
prompt$ psql -U ln_admin -h 192.168.1.11 -d lndb
```

On your local computer:

```text
prompt$ psql -U ln_admin -h 127.0.0.1:5432 -d lndb
```
