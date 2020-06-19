---
title: Import Barcode IDs
categories:
  - Workflows
tags:
  - barcode ids
  - import
comments: true
---


Barcode IDs are imported by plate number. The [files page](/software/file-formats) provides details on the file format, with a 10 plate [sample file](/software/barcodeids/barcodes.txt) available. Navigate into the project of interest and highlight the plate set of interest. From the menu select Utilities/Import Barcodes:

{% asset_img barcode-menu.png %}

The import barcode dialog box will open - fill in information and click OK:

{% asset_img dialog.png %}

The dialog calculates the number of lines expected by querying the plate count for the plate set. The example barcode import file looks like:

```text
plate 	barcode.id
1	LN000001
2	LN000002
3	LN000003
4	LN000004
5	LN000005
6	LN000006
7	LN000007
8	LN000008
9	LN000009
10	LN000010
```
Once imported, barcode ids will appear in the plate table:

{% asset_img table.png %}

Example barcode ID import file:

|File name|Description|
|--|--|
|[barcodes.txt](barcodes.txt)|Ten 96 well plates in PRJ-10|
|[barcodes1000.txt](barcodes1000.txt)|1000 barcodes|





