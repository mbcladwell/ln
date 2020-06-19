---
title: Search
categories:
  - Features
tags:
  - search
  - query
  - filter
  - global
comments: true
---

{% asset_img search-head.png %}

|Button|Description|
|--|--|
|Refresh|Remove filter and restore table; also refreshes table post sort/select|
|Filter:/Clear:|Apply filter/Clear filter|
|Global|Perfom a global query using the term in the text box|

## Filter

Filter the current visible table.  The term entered in the text box is automatically wrapped with wild cards (% in PostgreQSL).  Filter is incremental as you type.  Once text is entered in the text box, the "Filter:" button is relabeled as "Clear:". To remove the filter and restore the original table press "Clear:" or "Refresh".  Refresh has the additional utility of restoring default sort order and removing any selections made on the table.

## Global Query

At any point during a filter, the "Global" button can be pressed to perform a global query across all entity tables (project, plate set, plate, sample, assay run, hit list) using the term in the text field.  Wells are not included in the global search to minimize output i.e. you can't search for well "A08".  In the Global Query table, labeled with an otherwise non-functional "Global Query" menu item, you can navigate into a row item by pressing the navigate down button.  Navigate Up brings you to the last enclosing entity that you visited. The table may be empty if previously you did not visit such entity (e.g. you never visited the plate table). Perform a global query for "PRJ" to get back to the project table and proceed navigating from there.

In the image below the search term "6" has been applied globally.  The filter text box is clear and ready for further filtering.

{% asset_img global.png %}

If the entity you are interested in is a hit list or assay run, the appropriate viewer will be opened.



