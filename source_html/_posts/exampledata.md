---
title: Example data sets
categories:
  - Workflows
tags:
  - example data
  - data
  - files for import
  - sample files
comments: true
---

LIMS*Nucleus example database contains plate sets, plates, hit list, assay runs and data.  The various projects are at different stages of completion to demonstrate the functionality of the application.

|Project ID|Description|Utility|
|--|--|--|
|PRJ-1|3 plate sets with 2 96 well plates each; includes data and hit lists|visualize data; visualize hit lists; group plate sets;|
|PRJ-2|1 plate set with 2 384 well plates each; includes data|create hit lists|
|PRJ-3|1 plate set with 1 1536 well plate|visualize data|
|PRJ-10|2 plate sets with 10 96 well plates each|add data;create hit lists; subselect plate sets|

<br><br>
### Files for import

Provide your own import files or use the supplied data below:

|Description|File|
|--|--|
|Accession IDSs for PS-1 ( 2 96 well plates, 4 controls) |[accs96x2controls4.txt](accs96x2controls4.txt)|
|ELISA data for 2 96 well plates with 4 controls col 12 (LYT-1)|[sample96controls4lowp2.txt](sample96controls4lowp2.txt)|
|HTRF data for 5 96 well plates with 8 controls col 12 (LYT-7)|[sample96controls8low5plates.txt](sample96controls8low5plates.txt)|
|A new 384 well layout with controls randomly placed|[controls8scatteredNoEdge384.txt](controls8scatteredNoEdge384.txt)|


### Statistics for example assay data files

|Assay Run|File|Response|mean(neg)|stdev(neg)|neg + 3SD|neg hits|mean(pos)|pos hits|
|--|--|--|--|--|--|
|AR-1|[ar1data.txt](ar1data.txt)|raw|0.1155|0.07|0.3255|53|0.586|7|
|||bkgrnd_sub|0.1146|0.0697|0.3236|53|0.5852|7|
|||norm|0.1807|0.1172|0.5322|48|0.9076|6|
|||norm_pos|0.1971|0.1236|0.568|51|0.9985|7|
|||% enhanced|NA|0|NA|NA|0|7|
|AR-2|[ar2data.txt](ar2data.txt)|raw|0.0945|0.0332|0.1942|125|0.567|6|
|||bkgrnd_sub|0.0922|0.0357|0.1993|117|0.5646|6|
|||norm|0.1381|0.0601|0.3184|115|0.8371|6|
|||norm_pos|0.1641|0.0703|0.3751|115|0.9959|6|
|||% enhanced|NA|NA|NA|NA|0|6|
|AR-3|[ar3data.txt](ar3data.txt)|raw|2850|2616.2951|10698.8853|89|24200|3|
|||bkgrnd_sub|2849.9978|2616.2935|10698.8781|89|24199.997|3|
|||norm|0.1102|0.1076|0.4331|78|0.8908|4|
|||norm_pos|0.1184|0.1095|0.4469|88|1|3|
|||% enhanced|NA|NA|NA|NA|0|3|
|AR-4|[ar4data.txt](ar4data.txt)|raw|0.109|0.066|0.3069|288|0.5649|27|
|||bkgrnd_sub|0.1076|0.066|0.3057|287|0.5635|27|
|||norm|0.1513|0.0956|0.438|291|0.794|36|
|||norm_pos|0.1906|0.1168|0.5411|288|0.9976|27|
|||% enhanced|NA|NA|NA|NA|0|27|
|AR-5|[ar5data.txt](ar5data.txt)|raw|0.0764|0.0512|0.23|857|0.5787|48|
|||bkgrnd_sub|0.0728|0.0512|0.2265|857|0.5752|48|
|||norm|0.0895|0.0629|0.2783|857|0.7067|48|
|||norm_pos|0.1259|0.0885|0.3914|857|0.9939|48|
|||% enhanced|NA|NA|NA|NA|0|48|


