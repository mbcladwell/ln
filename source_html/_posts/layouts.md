---
title: Layouts
categories:
  - Layouts
tags:
  - terminology
  - sample
  - target
comments: true
---

LIMS*Nucleus makes use of the following definitions:

**Sample:** Item of interest being tracked by LIMS*Nucleus, i.e. the item in wells.  Examples would be compounds, antibodies, bacterial clones, DNA fragments, siRNAs.

**Target:** the item with which the sample interacts, usually coated on the bottomn of the microwell plate e.g. the antigen for an antibody or the enzyme (target) of a compound.

{% asset_img  target-sample.png %}


When creating layouts there are three attributes that need to be defined:

|Entity|Attribute|
|--|:--|
|Sample|type, replication|
|Target|replication|

LIMS*Nucleus support 5 sample types:

|Type |ID|
|--|:--|
|unknown|1|
|positive control|2|
|negative control|3|
|blank|4|
|edge|5|

LIMS*Nucleus has twenty pre-defined layouts installed at the time of system installation. Custom sample layouts can be defined and imported by administrators.  A [sample layout import file](controls4col7.txt) that defines four control wells at the bottom of column 7 looks like:

```
well	type
1	1
2	1
3	1
4	1
5	1
...
51	1
52	1
53	2
54	2
55	3
56	4
57	1
58	1
...

92	1
93	1
94	1
95	1
96	1
```

When viewed in the layout viewer, the above file would provide the following sample layout:

{% asset_img 4incol7-96well.png %}

For every sample layout imported, an additional 5 layouts are created that define sample and target replication.  These layouts are discussed in detail on the [replication](../replication) page.

Here is a [sample layout import file](controls8scatteredNoEdge384.csv) that defines 8 controls in a 384 well plate, randomly scattered, excluding edge wells

{% asset_img random8in384.png %}

When reformatted into 1536, the layout will look like:

{% asset_img random8in1536.png %}


