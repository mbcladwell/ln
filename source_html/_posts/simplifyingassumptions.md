---
title: Simplifying Assumptions
categories:
  - null
tags:
  - assumptions
  - simplifying assumptions
comments: true
---


* Always use the 3 character well name
A01, not A1 


* Default to tab delimitted text
In some cases comma or tab delimitted will be offered as an option. Proprietary formats are avoided. 


* Plates are always filled by column
Well number is derived from the order of filling.


{% asset_img fill-by-column.png %}

*Reformatting is performed in the "Z" pattern
Quadrants are numbered in the Z pattern.

{% asset_img zpattern.png %}

* Plate sets contain plates of the same format and layout

* Always import a full plate of data, even if the plate isn't full e.g. a data file for three 384 well plates should have 3*384=1152 rows even if the third plate isn't full.  Only control wells and unknown wells with samples will be processed.




