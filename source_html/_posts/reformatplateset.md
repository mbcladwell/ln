---
title: Reformat a Plate Set
categories:
  - Workflows
tags:
  - reformat
comments: true
---

Read the [reformat overview](/software/reformat)

1. Navigate to the project containing the plate set to be reformatted
2. In the plate set view, highlight the plate set to be reformated
3. From the Utilities menu select "Reformat"

{% asset_img  menu.png %}


4. The dialog box that appears is divided into 2 sections: "Source Plate Set" provides details of the source, in this case PS-1.  "Destination Plate Set" requires configuration by the user.  The predicted number of plates and the new plate set layout depend on the replication selections made. Review [replication](/software/replication) for background information. Fill in the required information and click OK.

{% asset_img  dialog.png %}

The new plate set, PS-24, is available in the plate set list:

{% asset_img  results.png %}

Drill down to see that there are ten 384 well plates. The original plate set contained 20 plates in LYT-7 that were reformatted in duplicate using LYT-10.  Use the [layout viewer](/software/layoutviewer) to identify the potential destination layouts for LYT-7. LYT-10 is one option for duplicate samples, with the target being the same across the plate (4T - same target in all 4 quadrants).

{% asset_img  layout-10.png %}

This requires ten 384 well plates with the duplicate samples side-by-side using the replication pattern 2S4T:

{% asset_img  dups.png %}

To confirm the samples are in this format, drill down to wells/samples and sort the samples by clicking on the "Sample" column header. Confirm that the twp replicates of SPL-1 are in neighboring wells A01, A02.

{% asset_img  A01A02.png %}

