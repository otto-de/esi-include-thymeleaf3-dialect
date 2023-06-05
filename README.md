![Build](https://github.com/otto-de/esi-include-thymeleaf3-dialect/workflows/Build/badge.svg)

# 1. About

This library provides a thymeleaf3 dialect that resolves <esi:include> tags.

When property `esiinclude-thymeleaf-dialect.dialect-enabled` is `true`, esi-includes will be resolved and their responses put into the output stream of the webpage.

# 2. Usage

* Add a dependency to this library to your project:
  `implementation de.otto:esi-include-thymeleaf3-dialect:<version>`

* Provide a Fetch function that is used to resolve esi includes.
  If a dependency to `com.ning:async-http-client` is present, such a function is provided automatically.
 
  Otherwise you need to provide a spring bean with type Fetch yourself. The function takes the source url as its argument and returns a `Response` object.
 
  Make sure that your Fetch-function handles redirects, as this library does not do it.
  
* Set property `esiinclude-thymeleaf-dialect.dialectEnabled`  to `true` so that the esi dialect is actually used
  
* Optional: Set property `esiinclude-thymeleaf-dialect.prefixForRelativePath` to `[http|https]://<hostname>` to prefix relative esi:include src urls.    

* Optional: Set property `esiinclude-thymeleaf-dialect.proxyEnabled` to `true` to enable and start a local proxy. This proxy sets the header `Access-Control-Allow-Origin` to `*` so that problems with CORS are prevented.
 
  The following properties configure the proxy:
  - `proxyRedirectProtocol` - protocol used for redirects, default is `https`
  - `proxyRedirectHost`- host where to redirect to
  - `proxyPort` - port where the proxy is listening, default is `8085`




# 3. Release Notes

## Version 2.0.0
* Build with java 17 / gradle 8
* Update to spring boot 3 / spring 6

## Version 1.1.0
* Add property `enabled-uris`. 
 It's now possible to restrict resolving of esi includes. When set only esi includes that are contained in responses of request uris that start with one of the configured `enabled-uris` are resolved. 
When not set, all esi includes are resolved as before.

## Version 1.0.0
* Updates to Spring 5.x, Spring Boot 2.x

## Version 0.2.5
* Remove default fetch configuration

## Version 0.2.4
* Provide default fetch implementation even when `dialect-enabled` is false since it is used by both the proxy and the esi dialect.

## Version 0.2.3
* Fix logging

## Version 0.2.2
* Catch BindException when proxy port is already in use. This may happen during spring integration tests when spring caches multiple application contexts.

## Version 0.2.1
* Stop proxy on context shutdown in @PreDestroy

## Version 0.2.0
* Activate esi dialect by property `esiinclude-thymeleaf-dialect.dialect-enabled` instead of by spring profiles 

## Version 0.1.1
* Add properties `esiinclude-thymeleaf-dialect.proxy-redirect-host` and `esiinclude-thymeleaf-dialect.proxy-redirect-scheme` to make redirect host and scheme configurable.

## Version 0.1.0
* Extract EsiContentResolver into separate Bean.
* Add proxy to avoid CORS errors when resources are loaded within an esi-included page. Proxy can be enabled using the property 
`esiinclude-thymeleaf-dialect.proxy-enabled`. The port can be set with `esiinclude-thymeleaf-dialect.proxy-port`, default is `8085`.

## Version 0.0.4
* Add spring properties bean for property `esiinclude-thymeleaf-dialect.prefixForRelativePath` 
so that you get syntax highlighting and code completion in your application*.yml files in your IDE.
  
  This property may now also be written "`prefix-for-relative-path`"
 

## Version 0.0.3
* Add javadoc
* Rename variables and httpClient to Fetch
* Rename property `esiinclude-thymeleaf-dialect.hostname` to `esiinclude-thymeleaf-dialect.prefixForRelativePath`

## Version 0.0.2
Provide asyncHttpClient as httpClient when com.ning:async-http-client dependency is provided

## Version 0.0.1
Initial version
