---
title: Hit Identification
categories:
  - Workflows
tags:
  - hits
  - scatterplot
  - algorithms
  - threshold
comments: true
---

Hit identification can be accomplished using one of three different methods:

## 1. Select an algorithm during data import

Check the "auto-identify hits using algorithm" check box and select the algorithm from the drop down.  Hits will be identified upon import and you will be prompted to name the hit list.

{% asset_img import-assay-data.png  %}


## 2. In the scatterplot view, select a threshold and generate a hit list

Threshold can be set using the slider, selecting an [algorithm](/software/algorithms), manually entering a threshold or desired number of hits.  Desired number of hits is auto-calculated if not set directly.  Inspect the plot and select "Generate hit list" when satisfied with the selection.

{% asset_img scatter-2plates-4controls.png  %}


## 3. Export data and use external data analysis to identify hits. Import the hit list.

Post data import, annotated data can be exported for visualization using other software.  A hit list can be compiled external to LIMS*Nucleus and then imported.



