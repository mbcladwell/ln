---
title: Initialize a new server
categories:
  - Developer
tags:
  - initialize
  - PostgreSQL
comments: true
---

Once you have decided to initialize your own instance of PostgreSQL you must configure a [limsnucleus.properties file](/software/props) and launch LIMS\*Nucleus database utilities dialog.  Do so by indicating init=true in the limsnucleus.properties file.


This will launch the [database utilities](/software/dbutilities/) dialog box, with multiple fields prepopulated with the data in the properties file.  Buttons in the "Database Setup" tab will allow creation of tables and stored procedures, as well a insertion of demonstration data if desired.

{% asset_img   %}

Once the database has been created/populated, shut down the client. In the limsnucleus.properties file change the init parameter back to false: init=false so the client launches in normal (and not database initialization) mode.

Under routine operation, database administrators have access to the database utilities dialog box under the "Admin" menu item.

The normal login routine requires user lookup in a users table.  If the database is deleted, the client will be unable to validate a user trying to launch the application.  In this scenario an administrator must:

* Set init=true in limsnucleus.properties file allowint direct access to the [database utilities](/software/dbutilities/) dialog
* Create all tables and example data if desired
* Set init=false in limsnucleus.properties
