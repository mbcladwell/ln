---
title: Tutorial 3, target layout definition
categories:
  - Tutorials
tags:
  - tutorial 3
  - target
  - target layout
comments: true
---

In [tutorial4](/software/tut4/) we will run through the canonical workflow using duplicate targets.  We could use the built in defaults target1, target2 etc., but we will assume you have a companion database that holds target specific information, and we wish to merge our assay data with the target information. 

Assume we are screening transferrin receptor (CD71) antibodies against the receptor and a negative control - BSA.  First we need to register the targets in our system.  Use the target import file defined [here](targets.txt).  Select the menu item Admin/Bulk Target Import, (Note: if you are using the elephant sql demo database these targets have already been imported for you.) and select the targets.txt file when prompted.  The targets are now registered within project 10 and available in the targets dropdown. 

{% asset_img menu.png %}


Navigate into project 10 and select Utilities/Targets/Create Target Layout.  Select the duplicates radio button and select huCD71 for quads 1,3 (noting that target duplicates are always in the same column, sample duplicates in the same row). Select BSA for quads 2,4.


{% asset_img layoutdialog.png %}

The layout will now be available during plate set creation or reformating within project 10:

{% asset_img dialog2.png %}

Note that "Tutorial 3 target layout" is only available in project 10 and only when target layout is set to "2", which is only available when sample replication is set to 2 or 4.

[Tutorial 4](/software/tut4/) will use this target layout with a new plate set created by reformatting.


