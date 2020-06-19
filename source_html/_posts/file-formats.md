---
title: File Formats
categories:
  - File Formats
tags:
  - file
  - import
  - export
comments: true
---

## File characteristics

Tab delimitted ASCII text 
Header required 
Column names must be all lower case 
Column name spelling is critical 
Column order is critical 



## Data import

Plate order is defined by the plate set. 
Import plates must be in the same order as the plate set

```text
plate	well	response
1	1	0.293825508745667
1	2	0.114021462999455
1	3	0.238314598659791
1	4	0.396061216051141
1	5	0.296039032793309
1	6	0.442385430319253
```

## Accession ID import
```text
plate	well	accs.id
1	1	AMRVK5473H
1	2	KMNCX9294W
1	3	EHRXZ2102Z
1	4	COZHR7852Q
1	5	FJVNR6433Q
1	6	WTCKQ4682U
```
## Barcode ID import
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
## Bulk target import
```text
project	target 	description	accession
1	muCD71	Mouse transferrin receptor	FHD8SU29
1	huCD71	Human transferrin receptor	JDHSU789
1	cynoCD71	Monkey transferrin receptor	KSIOW8H3
1	BSA	Bovine serum albumin	KEUI87YH
2	Lysozyme	Lysozyme	KDJFG98D
2	GAPDH	Glyceraldehyde Phosphate Dehydrogenase	KFIIOD09
2	ICAM4	ICAM 4 integrin	KL0OIE7U
2	IL21R	IL21 receptor	KOI89IUY
```





