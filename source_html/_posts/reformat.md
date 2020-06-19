---
title: Reformat Overview
categories:
  - Workflows
tags:
  - reformat
comments: true
---


Reformat is an operation performed on plate sets. Reformat will collapse four 96 well plates into a single 384 well, providing 1 replicate per sample. It is also possible to generate duplicates or quadruplicates of each sample. The plate layout of a reformatted destination plate is predetermined by the layout of the source plate, with some examples shown below. Source plates are loaded into destination plates in [plate set order](/software/terminology), following the [Z pattern](/software/simplifyingassumptions) through the [quadrants](/software/quadrants).


| 96 well source plate | 384 well destination plate|
|---------|--------|
|{% asset_img p96con4.png %}|Singlecates<br>{% asset_img p384con4rep1.png %}|
|| Duplicates<br>{% asset_img p384con4rep2.png %}|
|| Quadruplicates<br>{% asset_img p384con4rep4.png %}|


The same patterns apply when reformatting 384 well plates into 1536
