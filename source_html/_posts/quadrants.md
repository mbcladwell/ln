---
title: Quadrants
categories:
  - Layouts
tags:
  - layouts
  - reformat
  - quadrants
comments: true
---

When reformatting 96 well plates into 384 with fixed tip 96 channel liquid handling robot, the layouts of the destination plate is predetermined and matches the source layout, only replicated four times. Each source plate is deposited into a "quadrant" of the destination plate, such that a quadrant is defined as the wells across the destination plate that accomodate the samples from one source plate.

In the image below, quadrants are indicated for wells A01, A02, B01, and B02. Additional wells that belong to quadrant 1 are indicated.  Note that quadrant 1 wells extend across the 384 well plate down to well O23.

{% asset_img quadrants.png  %}

The following images explicitly define the indicated quadrant with a filled in square:

## Q1
<img src="q1.png" width="500" height="250">
## Q2
<img src="q2.png" width="500" height="250">
## Q3
<img src="q3.png" width="500" height="250">
## Q4
<img src="q4.png" width="500" height="250">

A simplifying assumption made by LIMS*Nucleus is that during a reformatting operation, source plates are added sequentialy in a Z pattern such that plate 1 is deposited into quadrant 1, plate 2 into quadrant 2, etc.:

{% asset_img reformat-z-pattern.png  %}

The process of transferring four 384 well plates into a 1536 well plate is analogous to what is described above.
