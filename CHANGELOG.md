* 2.1.1

    * Query string parsing no longer breaks with invalid `=?` input
    * Returning null from an onBeforeSend callback now cancels the send correctly
    * Guards against UnknownHostException when getting the machine name
    * Fixes test scope for junit dependency



- 2.1.0: Add and expose OnBeforeSend and setGroupingKey implementations

- 2.0.1: Improved logging

- 2.0.0: Fixed a bug with incorrect casing of user fields - breaking change for RaygunIdentifier as several of its fields are now private.

- 1.6.1: Fix incorrect Play2 packaging

- 1.6.0: Envionemnt log statements now guarded and namespaced under Raygun4java.enironment; upgrade Play to 2.3/support Scala 2.10/2.11

- 1.5.0: Added enhanced user data support with SetUser(RaygunIdentifier) - this deprecates SetUser(string)

- 1.4.2: Added Scala RequestHeader overload in constructor for Play 2 provider (rg4j-play2 is now at 0.4.4)

- 1.4.0: Added alpha version of Play 2 provider

- 1.3.2: Fix in core: check for NRE when getUser called from RayguntMesssageBuilder without setUser called first; this happened when RaygunServletMessageBuilder was used directly

- 1.3.1: Client message update

- 1.3.0: Added Async Send (beta) functionality to *webprovider* package; populate HTTP Headers and Form data in web context

- 1.2.7: Fixed bug when using core in Google App Engine threw an exception that wasn't caught when attempting to get environment data. Clarified documentation

- 1.2.6: Version now automatically reads Specification-Version then Implementation-Version in manifest, and provided method for manually specifying version

- 1.2.5: Fix a bug where the package used to populate the environment data was not available in certain runtime environments (OSGi, some JVMs)

- 1.2.4: Refactored webprovider package; added SetUser method for unique user tracking; added authenticated proxy support.

- 1.2.3: Added tags and user custom data method overloads for Send. See usage in the updated Sampleapp class.

- 1.2.1: Breaking change: Completed change from ant to maven for packaging and building. The three parts are now maven modules, core, webprovider and sampleapp. The main provider is now located in the *core* namespace, and the JSP and Servlet module is located in *webprovider*.

- 1.0: Maven package refactor.

- 0.0.1: Initial version.