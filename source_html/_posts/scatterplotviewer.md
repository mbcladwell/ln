---
title: Scatter Plot Viewer
categories:
  - Viewers
tags:
  - scatterplot
comments: true
---

Launch the Scatter Plot Viewer from within the [Assay Run Viewer](/software/assayrunviewer) using the "Plot" button. Be sure the assay run of interest has been selected:

{%asset_img assay-run-viewer.png %}

The Scatter Plot Viewer will launch, displaying the data associated with the selected assay run:

{% asset_img scatter.png %}

The scatter plot viewer allows you to generate a hit list with a variety of methods available to select hits.  Description of the fields follows:

|Field|Description|
|--|--|
|Algorithm|Select an [algorithm](/software/algorithms) that will be used to identify hits|
|Response|Select a [response](/software/algorithms) that will be used to identify hits|
|Threshold|Manually enter a threshold above which all responses will be considered a hit; hit count will be calculated automatically|
|Number of hits|Manually enter the desired number of hits - threshold will be automatically set|

There is also a slider to the right of the plot that allows manual threshold selection.
