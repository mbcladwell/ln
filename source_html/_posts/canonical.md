---
title: Canonical Workflow
categories:
  - Workflows
tags:
  - workflows
  - rearray
  - reformat
comments: true
---

Below is the canonical workflow that LIMS*Nucleus is designed to handle.

1. Create a [plate set](/software/createplateset).
1.5 Possibly [reformat](/software/reformat/) the plate set into higher density plates for assay.
2. [Create assay plates](/software/createplateset/) and run an [assay](/software/assayrunviewer/).  [Apply assay data](/software/importassaydata/) to the assay plate set.
3. [Identify hits](/software/hitidentification/)
4. [Rearray hits](/software/rearray/) into a new plate set
5. (Reformat hits into higher density plates), run hits in a secondary assay, identify hits, rearray, etc.

[Tutorial 1](/software/tut1/) will walk you through this workflow.

{% asset_img workflow.png %}

[Next>> Evaluate](/software/download)



