---
title: Algorithms
categories:
  - null
tags:
  - algorithms
  - data analysis
comments: true
---

## Processing steps

1. Import data, setting all negative values to 0.

   On a plate by plate basis:

2. Calculate the average of all wells labeled "blank" to obtain plate specific backgound signal
3. Subract backgound from all signals to obtain background subtracted values (bkgrnd_sub below) which are used in all further calculations
4. Set all background subtracted values that are less than zero to zero
(4.5 For layouts utilizing duplicates ([2S4T, 4S2T](/software/tut2/)), average the duplicates)
5. Calculate norm, norm_pos, p_enhance as described below


## Background subtraction, normalization

Upon data import, raw values are stored and processed as described above, then the calculations below are performed to yield additional columns of stored data.


|column|Description|
|--|--|
|raw|imported raw data|
|bkgrnd_sub|mean of all wells annotated "blank" subtracted from each raw value;<br> {% math %}unknown - mean(blank){% endmath %}|
|norm|all values normalized to the maximum of the background subtracted values annotated as "unknown";<br>{% math %}\frac{unknown - mean(blank)}{max(unknown - mean(blank))}{% endmath %}|
|norm_pos|all values normalized to the mean of the background subtracted values annotated as "positive"; $\frac{unknown - mean(blank)}{mean(positive - mean(blank))}$|
|p_enhance|Percent enhancement over the positive control;<br> 100*($\frac{(unknown - mean(blank))-mean(negative - mean(blank))}{mean(positive - mean(blank)) - mean(negative - mean(blank))}-1)$|


## Hit identification


### Algorithm


|Label| Hit threshold|
|--|--|
|mean(neg) + 3SD|{% math %}mean(negative - mean(blank)) + 3*stdev(negative - mean(blank)){% endmath %}|
|mean(neg) + 2SD|{% math %}mean(negative - mean(blank)) + 2*stdev(negative - mean(blank)){% endmath %}|
|>0% enhanced|{% math %} unknown - mean(positive) > 0{% endmath %}|
|Top N|Highest N responses from unknowns|


<br><br>
### References

Sittampalam GS, Coussens NP, Brimacombe K, et al., editors. Assay Guidance Manual [Internet](https://www.ncbi.nlm.nih.gov/books/NBK53196/). Bethesda (MD): Eli Lilly & Company and the National Center for Advancing Translational Sciences; 2004-.

Brian P. Kelley, 1 Mitchell R. Lunn, 1 David E. Root, Stephen P. Flaherty, Allison M. Martino, and Brent R. Stockwell; A Flexible Data Analysis Tool for Chemical Genetic Screens, <b>Chemistry & Biology 11:1495–1503</b>, November, 2004

