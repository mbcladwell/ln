---
title: Import Accession IDs
categories:
  - Workflows
tags:
  - accession ids
  - import
comments: true
---

Accession IDs are imported by plate number/well number. The [files page](/software/file-formats) provides details on the file format, with a 2 plate [sample file](/software/accessionids/accs96x2controls4.txt) available. Navigate into the project of interest and highlight the plate set of interest. From the menu select Utilities/Import Accessions:

{% asset_img accession-menu.png %}

The import accession dialog box will open - fill in information and click OK:

{% asset_img dialog.png %}

The dialog calculates the number of lines expected by querying the sample id count for the plate set.  For example a plate set of two 96 well plates with 4 control wells per plate (no sample ids for controls) will require 2x(96-4)=184 accession ids. The control wells should not be included in the import file.  For example a portion of the accession import file looks like:

```text
plate	well	accs.id
1	1	AMRVK5473H
1	2	KMNCX9294W
1	3	EHRXZ2102Z
1	4	COZHR7852Q
.
.
.
1	89	BKLUY4714F
1	90	VHDTN3829S
1	91	XUTEZ7832G
1	92	EGTKM4119P
2	1	GWGJT9769W
2	2	TKSUP0061X
2	3	HLVOR2656F
2	4	TIYAO0501Y
2	5	YBBDN6102H
.
.
.
```
Once imported, accesion ids will appear in the well table:

{% asset_img table.png %}

Example accession ID import files:

|File name|Description|
|--|--|
|[accs96x2controls4.txt](accs96x2controls4.txt)|Two 96 well plates with 4 controls in column 12|
|[accs384x2controls8.txt](accs384x2controls8.txt)|Two 384 well plates with 8 controls in column 24|
|[accs1536x2controls16x2.txt](accs1536x2controls16x2.txt)|Two 1536 well plates with 16 controls, in duplicate, in columns 47, 48|




