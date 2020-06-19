---
title: Useful SQL commands
categories:
  - Developer
tags:
  - sql
  - postgres
comments: true
---

Not all functionality is available through user friendly dialog boxes. Some modifications are performed using SQL command operating directly on the database. Below are some useful commands that can be executed at the PSQL prompt (lndb=>).

|Purpose|SQL command|
|--|--|
|View all users|lndb=>select * from lnuser;|
|Add a plate type|lndb=>INSERT INTO plate_type (plate_type_name) VALUES ('assay');|
|Add an assay type|lndb=>INSERT INTO assay_type (assay_type_name) VALUES ('ELISA');|
|Add a well type|lndb=>INSERT INTO well_type (name) VALUES ('positive');|
|Delete example data|lndb=>TRUNCATE project, plate_set, plate, hit_sample, hit_list, assay_run, assay_result, sample, well, lnsession RESTART IDENTITY CASCADE;|
|Refresh example data|lndb=>\i create_data.sql|
|Query session information|lndb=>select lnuser.lnuser_name,  lnsession.updated from lnsession, lnuser where lnsession.lnuser_id=lnuser.id;|


