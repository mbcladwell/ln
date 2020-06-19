---
title: Architecture
categories:
  - Developer
tags:
  - Java
  - PostgreSQL
comments: true
---

LIMS*Nucleus makes use of a client/server design.  The client uses JDBC to communicate with the server. There is no middleware/ORM.  A minimum number of libraries were included as dependencies including:

{% asset_img  arch.png %}


Client: Java 1.8 using Swing for interface components and WebStart for delivery.

Server: PostgreSQL 10

Though Postgres is reported to be the closest in syntax to Oracle amongst the large open source database providers, numerous methods are implemented using stored procedures that make use of Postgres specific extensions.  It is unlikely that the Postgres installations scripts will work with Oracle.

Postgres extensions as well as extensive use of serial ids make LIMS\*Nucleus incompatible with MySQL.



[Next: ]()
