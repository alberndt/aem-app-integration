
1 Setup Phase
============================

lkilik


1.1 Define External Application
---------------------------------



 

2 Integration Phase
============================



2.1 Init
---------------------------------

 - [ ] trigger integration (time-based, on-the-fly)
 - [ ] load definition data
 - [ ] query instances | load pre-defined instances
 - [ ] evaluate context for all instances


2.2 Loading Cycle
---------------------------------
 - [ ] load application.properties

2.3 Resource Processing
---------------------------------

### 2.3.1 Generic

 - [ ] Verify

### 2.3.3 application.properties

 - [ ] calculate unique entry-point urls (HTML snippets)
 - [ ] load all entry-points
 - [ ] load follow-up resources

### 2.3.3 HTML

 - [ ] Verify HTML
 - [ ] Extract snippet
 - [ ] Verify snippet
 - [ ] Extract static resources
 
### 2.3.4 JS


### 2.3.5 CSS


### 2.3.6 Binary Resources (e.g. bitmaps, fonts, ...)


 
 
2.4 Post-Processing
---------------------------------
 - [ ] A/B Switch
 - [ ] Clear Dispatcher Cache
 - [ ] Calculate Overall Status
 - [ ] Next Run


 


application def (OSGi Config)

-> ApplicationInfo (remotely loaded application-info.json)



application instance

(via application instance provider + context providers)


html-snippet (per instance)

html-snippet extractor


js + css

other resources




3 Runtime
============================

### 3.1.1 deliver html snippet

1. Check for dynamic context (live, cached, prefetch)

#### Steps

 - instance
 - application.properties
 - instance context
 - entry-point
 - html-snippet
   
    

 
 
### 3.1.2 deliver static resources
 
### 3.1.3 proxy dynamic-urls

