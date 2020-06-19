---
title: Systems
categories:
  - Overview
tags:
  - monoliths
  - systems
comments: true
---


A systems approach involves the integration of multiple independant commercial and custom software products to work in unison towards a common goal.  A systems approach allows flexibility by allowing for the upgrade or discard and replacement of individual components as requirements change.  

{% asset_img  systems2.png %}


## Advantages

* Flexible; can evolve as process evolves
* Best of breed components can be used
* Portability of knowledge (Spotfire, R, SQL)
* Adaptable to [containerization](https://www.nature.com/articles/s41587-019-0136-9)


## Disadvantages

* Components on different upgrade cycles
* Components use different technologies with scattered expertise
* Configuration challenges: missing libraries, auxilliary software
* May depend on external network connectivity
* User training can be challenging
* Integration can be challenging


### References

[Microservices as innovation enablers](https://hackernoon.com/why-microservices-are-the-new-innovation-enablers-for-enterprises-6a4c637fd901)  best practices == common practices
[Split the monolith](https://hackernoon.com/why-you-should-split-the-monolith-e946f57db38c)
[Trulia switches to "Islands"](https://techcrunch.com/2019/01/05/how-trulia-began-paying-down-its-technical-debt/)

[A contrarian's (with vested interests) view](https://hackernoon.com/why-you-should-not-switch-to-microservices-6f0bcd98ab68)

### Related

Case study of monolith implementation: [Why Doctors hate their computers](https://www.newyorker.com/magazine/2018/11/12/why-doctors-hate-their-computers) Discusses feature creep and the "Tar Pit"

[Proprietary IT give big companies their edge.](http://www.overcomingbias.com/2018/07/compulsory-licensing-of-backroom-it.html)



[Rob Brigham](https://hackernoon.com/the-coders-axiom-7881e88d495d?source=rss----3a8144eabfe3---4), Amazon AWS senior manager for product management:  "Now, don’t get me wrong. It was architected in multiple tiers, and those tiers had many components in them. But they’re all very tightly coupled together, where they behaved like one big monolith. Now, a lot of startups, and even projects inside of big companies, start out this way. They take a monolith-first approach, because it’s very quick, to get moving quickly. But over time, as that project matures, as you add more developers on it, as it grows and the code base gets larger and the architecture gets more complex, that monolith is going to add overhead into your process, and that software development lifecycle is going to begin to slow down."




[Next>> Features](/software/features)
