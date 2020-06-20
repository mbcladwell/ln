---
title: Tutorial 2 - canonical workflow with sample replicates
categories:
  - Tutorials
tags:
  - tutorial 2
  - replicates
comments: true
---

[Tutorial 1](/software/tut1/) covers in detail steps that are glossed over in this tutorial.

Looking at available layouts there are 2 that offer replication - 2S4T and 4S2T:

{% asset_img layouts.png %}


2S4T: Entire plate coated with the same target, samples in duplicate.  Note that sample replicates are in the same row.

4S2T: Each sample tested in duplicate on 2 targets. Note that target replicates are in the same column. (see [tutorial 3](/software/tut3) )

Following [tutorial 1](/software/tut1/) I will rearray the 10 plates of PS-6 into a new plate set applying the 2S4T layout. 10 96 well plates in duplicate will requires 5 384 well plates:

{% asset_img dialogreformat.png %}

Note that selections made in the sample replication dropdown limit the options available in the other dropdowns. View the [target layout](/software/targetlayout/) for a discussion on optional; target layouts, which are only used for data annotation.

Use the data file [plates384x5_2S4T.txt](/software/tut2/plates384x5_2S4T.txt) which contains demonstration data for 5 384 well plates.  The data has been generated such that replicates are side by side in the same row and are +/-10% of each other. In the graph below replicates for one plate are color coded and appear at the same X value:

{% asset_img repsplot.png %}

Looking at the response trellised by plate and colored by sample type:

{% asset_img responseggplot.png %}

Associate the data with the plate set:

{% asset_img dialogassoc.png %}

In the assay run viewer highlight the assay run and press the plot button. Note that the associated hit list HL-8 has 155 hits.

{% asset_img assayrunview.png %}

Responses are plotted in decreasing order with controls color coded. Not that if you select normalized response with a hit threshold set to mean(background)+/-3SD, 155 hits will be identified.  The hit list could have been created here, but was already auto-generated on import.

{% asset_img scatter.png %}

Looking at the hit list view you can see that PS-6, 96 well plates contain all 155 hits that can be rearrayed into two 96 well plates.

{% asset_img hitlistview.png %}

Once rearrying is complete, creating PS-11, that plate set is annotated with a worklist indicating that it was generated by a rearray activity.  The worklist can be used to physically rearray the samples into the new plate set PS-11.

{% asset_img worklist.png %}

[Tutorial 3](/software/tut3/) prepares a target layout for the [Tutorial 4](/software/tut4/) discussion on target replication.

