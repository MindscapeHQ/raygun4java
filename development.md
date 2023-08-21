# Raygun4Java Development Guide

Raygun4Java is a multi-module Maven project.
All Maven projects are specified via their pom files.
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

<b>Required:</b>
- Download [Maven](https://maven.apache.org/download.cgi).
- Download [sbt](https://www.scala-sbt.org/download.html) for compiling the Play provider. This is called via a Maven exec plugin during the `generate-sources` phase of the Play provider's pom.

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


# Releasing a new version

1. Run `mvn clean`.

2. Bump the version in the following places (include `-SNAPSHOT` here):

    - `pom.xml` > `version`.
    - `core/pom.xml` > `parent` > `version`
    - `playprovider/pom.xml` > `parent` > `version`
    - `webprovider/pom.xml` > `parent` > `version`
    - `webproviderjakarta/pom.xml` > `parent` > `version`
    - `sampleapp/pom.xml` > `parent` > `version`
    - `sampleJakartaEEApp/pom.xml` > `parent` > `version`

3. Run `mvn -P integration-tests verify` and ensure all tests pass.

4. Run `mvn clean install` for good measure.

5. **Prepare**:
    - Install GPG (GNU Privacy Guard): [Gpg4win](https://gpg4win.org/download.html). We only need the GnuPG component.
      - Ensure `gpg.exe` is added to your PATH. By default, this can be found at `C:/Program Files (x86)/GnuPG/bin/gpg.exe`.
    - Locate our **GPG passphrase** in 1Password.
    - Run `gpg --gen-key`, enter your details, and use the passphrase when prompted.
    - Locate our **Sonatype** login in 1Password.
    - Modify or create a `settings.xml` in your Maven `conf` or `~/.m2` directory. Add the following with credentials substituted in:
```xml
<settings>
   <servers>
      <server>
         <id>ossrh</id>
         <username>SONATYPE_USERNAME</username>
         <password>SONATYPE_PASSWORD</password>
      </server>
   </servers>
   <profiles>
      <profile>
         <id>ossrh</id>
         <activation>
            <activeByDefault>true</activeByDefault>
         </activation>
         <properties>
            <gpg.executable>gpg</gpg.executable>
            <gpg.passphrase>GPG_PASSPHRASE</gpg.passphrase>
         </properties>
      </profile>
   </profiles>
</settings>
```

6. **Prepare the release**:
    - Ensure all changes are committed to Git.
    - Run the Maven release prepare command:
      ```bash
      mvn release:prepare
      ```
    - You may be prompted for our GPG passphrase again.
    - This will ask you for the release version, tag name, and next development version, which you can likely leave as-is. Make sure the release version doesn't contain `-SNAPSHOT`. It will then make changes to your POMs and commit/tag them in Git.

7. **Perform the release**:
    - After preparing, you can perform the release by running:
      ```bash
      mvn release:perform
      ```
      This command will checkout the code from Git using the tag created in the prepare step, build the project and deploy it to the OSSRH repository.

8. **Release the artifacts on Sonatype**:
    - Once the artifacts are uploaded to OSSRH, you need to release them:
        - Go to [Sonatype OSSRH](https://oss.sonatype.org/).
        - Login with your Sonatype credentials.
        - Navigate to "Staging Repositories".
        - Find your artifact, select it, and click "Release".

9. **Clean up**:
    - After releasing, you can clean up the local checkout of the tag created by the `release:perform` command:
      ```bash
      rm -rf target/checkout
      ```

10. **Verify on Maven Central**:
    - Your artifacts will be synchronized from OSSRH to Maven Central. This might take some time. You can periodically check [Maven Central Repository](https://search.maven.org/) to see if your library has been updated. Search for your library, e.g., "raygun4java", to verify.

Remember:
- If anything goes wrong during the release process, you can use the `mvn release:rollback` command to rollback any changes made by the `release:prepare` command.
- Always ensure you have tested the project thoroughly before starting the release process.

Once these steps are completed, the Raygun4Java should be released and available on Maven Central!
