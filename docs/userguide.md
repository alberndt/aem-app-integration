---
layout: default
title: User Guide
description: This is just another page
---

## Roles
 - Application Developer (any technology, no AEM)
 - App Integration Manager (AEM power user, no developer)
 - AEM Developer
 - AEM Author

Application Developer

- Develop and deploy Web-Application with Meta-data

AEM Integrator

- Create interactively an AEM component per Application 

AEM Developer

- Create Context Provider

AEM Author



# How to integrate an web application

# Preparation - Global Config

OSGi Config:

- Create proxy for dynamic endpoints


# Step One: Integration Manager

The Integration Manager creates an OSGi Config for each application. Each config contains:

```properties
id=myapp

## URL to application.properties file
url=http://localhost:8080/myapp/application.properites


snippet-extractor=default|custom
+ validators
snippet-validator
mime-type validators: js, css, img





context-providers=sling,aem,myproject




## Type of cache
cache-provider-id=default|no-cache





# Properties 
default-properties=
```


- Static URL to an `application.properties` file
- Refresh/update scheme (default, read-through, custom)
- Context provider groups (sling, aem, ...)

Advanced topics:

- Content extraction rules (or extraction filters)
- Overwrite everything, that the application developers can configure
- Where is the application available (with clients / designs, context, ...)


# Step Two: Application Developer

Creates `application.properties` file with: 

```properties
html=/myapp/{lang}/my-app.html
dialog=/myapp/dialog.xml
```


- URL
  - relative URL to application.properties or absolute URL
  - Can contain variables
  
- Dialog
  - relative URL with variables dialog.{lang}.xml makes no sense. But a dialog.shop.xml and dialog.no-shop.xml makes sense,
    for a different setup, depending on the site (if a shop is available or not)
  
Variables can come from 3 sources:
- Context Providers (OSGi Service, resource in &rarr; context out) 
- User Dialog (via dialog.xml)
- (vergessen)  Embedded Content???

# Application developer: dialog.xml

- custom dtd
- simple (not the AEM dialog.xml)
- to define additional dialog tabs for the application component dialog
- additional features (like show-hide)





# Context Provider
(OSGi Service, resource in &rarr; context out)

get the context from AEM with a special selector




# Story 1: Basic web-application

AEM Author &rarr; Web Developer: Could you create for me?

AEM Author &larr; Web Developer: Here it is. Everything is in the ZIP-File.

    index.html
    js/myapp.js
    css/myapp.css
   

AEM Author &rarr; AEM Developer: Can you integrate this?

NO!!!!

Put it on a web-server: 


App Integration Manager

- create application config in AEM
- add URL for web-app (evtl. multiple per stage)
- optionally set basic auth info's

--- alternative ---

- create application config via OSGi config
- question: How to manage secret key's (e.g. DropBox key)

--> generates apps entries (components) (should be exported to GIT, and deployed afterward)
--> req: sync and out-of-sync messages (status: synced, not-synced)

```javascript
console.log("Hello World");
```


## How is the HTML snippet choosen?

html extract -> so the app is usable

## How to provide static and dynamic resources

cache manifest
static resources are mapped /ext/<app-id>/
dynamic resource are optionally proxied (e.g. for dev environments) 

url rewriting, 

## resource mapping

best develop with context /ext/<app-id>/, so no 

## How does it get updated

regular updates - every hour (configurable)
version check: an file with changes 
version file: eg. cache manifest with version key or timestamp
 (higher freq.)
 
update and cache headers
- if-not-changed-since or e-tag

other cache headers are ignored

## wcmmode Edit vs Publish

## generation of apache config

export apache config (e.g. mod_proxy)

# Story 2: Context Information

parameter --> xxx
dialog settings
context information

AEM Developer: Context Provider

# Story 3: Content to App Integration

inplace content
- AEM Author puts image into app (e.g. memory)
- Text snippets
- parsys (one column, full-with content)

# Story 3: Advanced Application- and API- Management

- Page frame
- Shared CSS + JS Frontend API's 
- Components
- Shared Data (e.g. User data)

 




# Validation Examples

App doesnt' uses certain static resources

- any jQuery
- any MyCompany Font-Resources
- any Given global css-files

CSS does not conflict with global settings

- e.g. All rules MUST start with certain selectors

JS does not conflict with GLOBAL namespace
