---
title: Replication
categories:
  - Layouts
tags:
  - layouts
  - replication
comments: true
---

LIMS*Nucleus handles five combinations of replication for samples and targets. In the table below, the pattern columns show the distribution of samples and target in the four quadrants of a 384 or 1536 well plate, where each color represents a unique sample (or target).

|Label|Sample pattern| Sample replication|Target Pattern| Target replication|N targets|N rep (Assay)|
|--|--|--|--|--|--|
|1S4T|{% asset_img unique.png %}|unique|{% asset_img quad.png %}|quadruplicates|1|1|
|2S2T|{% asset_img dups-horiz.png %}|duplicates|{% asset_img dups-vert.png %}|duplicates|2|1|
|2S4T|{% asset_img dups-horiz.png %}|duplicates|{% asset_img quad.png %}|quadruplicates|1|2|
|4S1T|{% asset_img quad.png %}|quadruplicates|{% asset_img unique.png %}|unique|4|1|
|4S2T|{% asset_img quad.png %}|quadruplicates|{% asset_img dups-vert.png %}|duplicates|2|2|


As an example consider an assay of eight 96 well plates that will be reformatted into 384 well plates for assay:

1S4T: Eight 96 plates will be reformatted into two 384 well plates (Four 96 per 384) with a unique plate in each quadrant. The 384 plates is coated with the same target in all four quadrants.  There is only one target (N Targets) and each sample gets assayed 1 time (N rep (Assay))

2S2T: Eight 96 well plates will be reformatted into four 384 well plates (two 96 per 384) with one sample plate replicated in quads 1,2 and a second replicated in quads 3,4.  The 384 well plates are coated with 2 antigens, antigen A in all odd columns and antigen B in all even columns.  There are two targets (antigens A and B - N targets) and each sample is assayed once on each target, (N rep (Assay)).  Because there are twice as many antigens being assayed as in the example above, twice as many plates are needed.

4S2T: Eight 96 well plates will be reformatted into eight 384 well plates (one 96 per 384) with one sample plate replicated in all four quadrants.  The 384 well plates are coated with 2 antigens, antigen A in all odd columns and antigen B in all even columns.  There are two targets (antigens A and B - N targets) and each sample is assayed twice on each target, (N rep (Assay)).

## Some combinations are avoided

For example these are <font color="red"> not an option</font>.

|Label|Sample pattern| Sample replication|Target Pattern| Target replication|N targets|N rep (Assay)|
|--|--|--|--|--|--|
|1S2T|{% asset_img unique.png %}|unique|{% asset_img dups-vert.png %}|duplicates|2|1|
|1S1T|{% asset_img unique.png %}|unique|{% asset_img unique.png %}|unique|4|1|

**1S2T**: Half the source plates are tested on one antigen, the other half on a different antigen.  This is better broken into 2 assays, one assay per antigen, or 2 X [ 1S4T ]

**1S1T**: Convert to 4 X [ 1S4T ], one assay per antigen.

## Simplifying Assumptions

Samples are always replicated horizontally ( quads 1,2 and 3,4)
Targets are always replicated vertically ( quads 1,3 and 2,4)
