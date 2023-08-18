# Raygun4Java Development Guide

Raygun4Java is a multi-module maven project.
All maven projects are specified via their pom files.
The parent pom file contains references to the following modules:
* <b>core</b>, the main functionalities for communicating errors to Raygun
* <b>playprovider</b>, a dedicated Play 2 provider for automatically sending Java and Scala exceptions from Play 2 web apps
* <b>webprovider</b>, a dependency for Java EE apps
* <b>webproviderjakarta</b>, a copy of webprovider for jakarta apps
* <b>sampleapp</b>, an example app for integration testing
* <b>sampleJakartaEEApp</b>, an example Jakarta app for integration testing

These modules can reference each other as dependencies.

## Setting up a dev environment
(Please) use your IDE of choice, e.g., IntelliJ.

<b>Important!</b> The sampleJakartaEEApp requires a Raygun API Key.
After you retrieve it from Raygun, create a config.properties file in `src/main/resources` of `sampleJakartaEEApp` that contains `raygun.apiKey=<YOUR-RAYGUN-API-KEY>`.

## Triggering the maven build lifecycle phases

Maven uses a build lifecycle, which you can look up online.
These will be executed for all modules in the project.
In the project's root directory, execute `mvn <phase>`.
For example, you can execute `mvn install`, runs *all* the previous phases and then, installs the package into the local repository, for use as a dependency in other projects locally.

To include the two integration-test modules in the lifecycle, add `-P integration-tests`, which specifies the profile in the parent pom file that contains them.
For instance, to run the tests execute `mvn -P integration-tests verify`.

## Webprovider and Jakarta modules
In 2018, the web APIs' namespaces where changed from `javax.*` to `jakarta.*`.
The webproviderjakarta is a copy of webprovider with just these namespaces changes plus appropriate dependencies.
If changes are made to one, ensure they are reflected to the other.
Finally, notice that unhandled exception catching is unlikely to work because exceptions are first caught and handled by middleware; hence, they never reach the Raygun filter.
This limitation is noted in the Raygun docs.