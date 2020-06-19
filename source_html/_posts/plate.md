---
title: Plate
categories:
  - Entities
tags:
  - plate
comments: true
---

Plates are one of three formats - 96, 384, or 1536 well
The plate system name in the format PLT-NNN is automatically assigned at creation
All plates are part of plate sets

Plates can be assigned a variety of types. Depending on the type, a plate may not contain samples. For example, assay plates are transient and discarded after data collection, so could not serve as the source for rearraying or replica plating.

Installed types are:

|Type|Description|Contain samples?|
|---|---|---|
|assay|contain associated data|no|
|rearray|created during a reformat operation|yes|
|archive|designated for storage|yes|
|master|original plate of samples|yes|
|daughter|result of replica plating or grouping operations|yes|
|replicate|result of replica plating|yes|



Plates are of various types - assay, rearray, glycerol, etc.
Plate types are to provide clarity to the user - no convention is enforced








{% asset_img  images/plate.png %}


