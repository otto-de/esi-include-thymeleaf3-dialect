[![Build Status](https://travis-ci.org/otto-de/esi-include-thymeleaf3-dialect.svg?branch=master)](https://travis-ci.org/otto-de/esi-include-thymeleaf3-dialect)

# 1. About

This library provides a thymeleaf3 dialect that resolves <esi:include> tags.

When spring profiles "local" or "prod" are active, esi-includes will be resolved and their responses put into the output stream of the webpage.

# 2. Usage

* Add a dependency to this library to your project:
  `compile de.otto:esiinclude-thymeleaf-dialect:0.0.1`

* Provide a Fetch function that is used to resolve esi includes.
  If a dependency to `com.ning:async-http-client` is present, such a function is provided automatically.
 
  Otherwise you need to provide a spring bean with type Fetch yourself. The function takes the source url as its argument and returns a `Response` object.
 
  Make sure that your Fetch-function handles redirects, as this library does not do it.
  
* Optional: Set property `esiinclude-thymeleaf-dialect.prefixForRelativePath` to `[http|https]://<hostname>` to prefix relative esi:include src urls.    


# 3. Release Notes

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