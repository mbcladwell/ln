---
title: Rearray
categories:
  - Workflows
tags:
  - worklist
  - rearray
comments: true
---

Rearraying is the process of transferring hits from one or more plates into a new plate set.  Hits are transferred in sample id order, into plates by column. In the example below, hits are the black wells across 3 plates, being transferred into a single plate:


{% asset_img  rearray.png %}

To initiate a rearray operation start by selecting a hit list in the hit list viewer, then press the "View" button:

{% asset_img assay-run.png %}

The contents of the selected hit list appear in the left panel, with a count at the top.  The right panel lists plate sets that contain some or all of the hits.  If you want to rearray all hits, choose a plate set with a count indicating all hits are present.  In this example any of the 3 available plate sets would be appropriate.  Press the "Rearray" button:

{% asset_img  hit-list.png %}

The rearray dialog box will appear, with an auto-generated description which you can edit. Fill in the required information and click OK.

{% asset_img dialog.png  %}

|Field|Description|
|--|--|
|Name|freeform text, a descriptive name|
|Description|auto-populated but editable|
|Number of plates|Auto calculated based on number of hits and plate layout|
|Format|96, 384 well plates are possible destination formats|
|Type|Rearray suggested but user selected|
|Layout|Choose a layout; layout "description" appears in this dropdown|

Once the new plate set has been generated, a refresh of the hit list viewer will show the new plate set available as a source for the samples of HL-21:

{% asset_img new-hit-list.png %}

Navigating to the plate set view, the newly created PS-14 in PRJ-1 is visible with its informative description.  There is also a worklist associated with the plate set because it was created using a rearray operation.  Now to physically transfer the samples from PS-8 to the new plates of PS-14, export the worklist to a liquid handling robot and run the transfer:

{% asset_img calc.png %}







