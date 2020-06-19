---
title: Create a Project
categories:
  - Entities
tags:
  - project
  - workflows
comments: true
---

Only administrators can create/edit/delete projects. Project is the only entity that can be deleted.  It will be deleted using a cascading delete, removing all plate sets, plates, samples, assay runs, and data that are part of the project.  Access the "Add Project" menu item under the Admin menu - visible only to administrators:


{% asset_img  project-add-menu.png %}


A dialog will appear with 2 fields, name and description. Pressing OK will add to and refresh the project list in the main browser.

{% asset_img  project-add-dialog.png %}

## Edit Project

Select the project to be edited, then from the menu bar select Admin/project/edit.  The edit project dialog box will be pre-populated with the selected project name and description.  Make changes and save.

{% asset_img  edit.png %}


## Delete Project

Select the project to be deleted and from the menu bar select Admin/project/delete.  The delet project dialog box will be presented for confirmation,

{% asset_img  delete.png %}

