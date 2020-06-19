---
title: Targets
categories:
  - Layouts
tags:
  - target
  - sample
  - layout
comments: true
---

For a definition of target see the [layouts](/software/layouts) page. Targets are primarily used to annotate data and assist with merging LIMS\*Nucleus data with data from other systems.  Defining targets is optional and if not done, generic "Target1", "Target2" labels will be used in output. Using targets requires three steps:

1. Register targets inividually or (administrator) import in bulk.
2. Define [target layouts](/software/targetlayout)
3. Apply layouts to plate sets

Defining layouts only makes sense when creating assay plate sets.  Apply the target layout during the reformating step.

 There are two methods of importing targets:

## Bulk import by an administrator

Under the admin menu item select "Bulk target import".  A file shooser dialog will appear.  Choose an import file with the format described below:

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
Here is an example target import file: [targets200.txt](targets200.txt)

Column header spelling, capitalization, and order are critical. Indicate the project to which the target should be associated in column one.  Import will fail if the project id is not in the database.  For targets that should be available to all projects, place "NULL" (no quotes) in the first column.  Only administrators can designate target project id as NULL during bulk import.  Note that currently there is no opportunity to update an accession at a later time should it be blank upon import.

## One at a time import by users

On the plate set menu bar select Utilities/Targets/Add New Target.  A dialog box will appear:

{% asset_img addtarget.png %}

Fill in the dialog.  Only name is required.  Press OK. The target is associated with the current project and is only available within that project. Once targets have been registered, they can be used in a [target layout](/software/targetlayout).


