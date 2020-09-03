



## Prefetch Caching Strategies



<dl>
<dt>size</dt> <dd>small</dd>
<dt>size</dt> <dd>small</dd>
</dl>


| Strategy        | Best for applications           |
| ------------- |-------------|
| size | small |
| code | infrequent changes |
| assets | infrequent changes |

### Basic 

prefetch every 10 minutes or every hour (scheduler)


### HTTP Caching

prefetch respects following http cache headers ....


### Version Resource

url for version resource . frequent query of a  version url - prefetch, whenever the resource changes.
best, if dynamic assets or very large applications with costly prefetch 


### On-the-fly caching 
cache static resources on the fly, if they are accessed



 