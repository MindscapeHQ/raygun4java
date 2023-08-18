# Raygun4Java: JakartaEE Sample App

This repository contains a minimal JakartaEE Java application that is used to assert that both handled and unhandled exception can be caught and transmitted to the Raygun ingestion pipeline.
The project contains two routes `/sampleJakartaEEApp/api/unhandled-exception` and `/sampleJakartaEEApp/api/handled-exception` that each test each of the desired features.
The project also contains two integration testing methods that independently call these routes to assert expected behavior.
The handled exception route returns an expected string iff `raygun.send` works as expected by returning status code 202, otherwise, a different string that causes the integration test fails.
That might be an indication that the raygun api key has not been set.
Check instructions next for this:

## How to run
Open the project with a suitable Java IDE, such as IntelliJ.
Create a config.properties file in `src/main/resources` that contains `raygun.apiKey=<YOUR-RAYGUN-API-KEY>`.
In the terminal run `mvn install`.
Confirm that traces appear at the Raygun app under the application api key provided.