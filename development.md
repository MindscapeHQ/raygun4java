# Raygun4Java Development Guide

Raygun4Java is a multi-module Maven project.
All Maven projects are specified via their pom files.
The parent pom file contains references to the following modules:

* <b>core</b>, the main functionalities for communicating errors to Raygun
* <b>playprovider</b>, a dedicated Play 2 provider for automatically sending Java and Scala exceptions from Play 2 web
  apps
* <b>webprovider</b>, a dependency for Java EE apps
* <b>webproviderjakarta</b>, a copy of webprovider for jakarta apps
* <b>sampleapp</b>, an example app for integration testing
* <b>sampleJakartaEEApp</b>, an example Jakarta app for integration testing

These modules can reference each other as dependencies.

## Setting up a dev environment

(Please) use your IDE of choice, e.g., IntelliJ.

<b>Required:</b>

- Download [Maven](https://maven.apache.org/download.cgi).
- Download [sbt](https://www.scala-sbt.org/download.html) for compiling the Play provider. This is called via a Maven
  exec plugin during the `generate-sources` phase of the Play provider's pom.

<b>Important!</b> The sampleJakartaEEApp requires a Raygun API Key.
After you retrieve it from Raygun, create a `config.properties` file in `src/main/resources` of `sampleJakartaEEApp` that
contains `raygun.apiKey=<YOUR-RAYGUN-API-KEY>`.

## Triggering the maven build lifecycle phases

Maven uses a build lifecycle, which you can look up online. The necessary lifecycle steps will be executed for all modules in the project.

Generally you can execute `mvn <phase>` in the project's root directory.

For example, you could execute `mvn install`. This will run *all* the relevant phases for building and testing the code base and then, installs the package into the local
repository, for use as a dependency in other projects locally.

To include the two integration-test modules in the lifecycle, add `-P integration-tests`, which specifies the profile in
the parent pom file that contains them.

For instance, to run the tests: execute `mvn -P integration-tests verify`.

## Webprovider and Jakarta modules

In 2018, the web APIs' namespaces where changed from `javax.*` to `jakarta.*`.

The `webproviderjakarta` is a copy of `webprovider` with just these namespaces changes plus appropriate dependencies.

If changes are made to one, ensure they are reflected to the other.

Finally, notice that unhandled exception catching is unlikely to work because exceptions are first caught and handled by
middleware; hence, they never reach the Raygun filter.

This limitation is noted in the Raygun docs.

# Releasing a new version

## Version bump

Currently, it is necessary to update the reported provider version in the `core` module's `RaygunClientMessage` class: `com/mindscapehq/raygun4java/core/messages/RaygunClientMessage.java`.

**Note: The following steps should not be necessary!** 
The previous release should have automatically set the next development version.

If that has not occurred for some reason:

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

## Prerequisites

1. Install GPG (GNU Privacy Guard): [Gpg4win](https://gpg4win.org/download.html). We only need the GnuPG component.
    - On Windows, ensure `gpg.exe` is added to your PATH. By default, this can be found
      at `C:/Program Files (x86)/GnuPG/bin/gpg.exe`.
    - On *nix systems, if not available already install via your package manager.
2. Locate our **GPG passphrase** in 1Password.
3. Run `gpg --gen-key`, enter your details, and use the passphrase when prompted.
4. After generating the key, you need to find out the ID of the GPG key. To list your GPG keys, execute:
   ```bash
   gpg --list-keys
   ```
   You'll see an output like:
   ```
   pub   ed25519 2023-08-21 [SC] [expires: 2026-08-20]
      YYYY
   uid           [ultimate] Your Name <your_email@example.com>
   sub   cv25519 2023-08-21 [E] [expires: 2026-08-20]
   ```
   Here, `YYYY` is your key ID.
5. Publish your public GPG key to a key server:
   ```
   gpg --keyserver hkps://keyserver.ubuntu.com:443 --send-keys YOUR_KEY_ID
   ```
6. Locate our **Sonatype auth token** in 1Password.
7. Modify or create a `settings.xml` in your Maven `conf` or `~/.m2` directory. Add the following with credentials
   substituted in:

```xml

<settings>
    <servers>
        <server>
            <id>raygun4java-repo</id>
            <username>SONATYPE_AUTHTOKEN_USERNAME_PART</username>
            <password>SONATYPE_AUTHTOKEN_PASSWORD_PART</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>raygun4java-repo</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.passphrase>GPG_PASSPHRASE</gpg.passphrase>
                <!-- Ensure Maven uses the correct SSH executable -->
                <maven.scm.provider.git.gitexe>C:/Windows/System32/OpenSSH/ssh.exe</maven.scm.provider.git.gitexe>
            </properties>
        </profile>
    </profiles>
</settings>
```

### Add an SSH key to GitHub

A verbose documentation from Github is available:

https://docs.github.com/en/authentication/connecting-to-github-with-ssh/checking-for-existing-ssh-keys
https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent
https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account
https://docs.github.com/en/authentication/connecting-to-github-with-ssh/testing-your-ssh-connection

The below steps are an abridged version of the GH documentation intending to provide an overview of the steps. If at any stage
you run into issues, please refer to the GH documentation.

1. **Generate SSH key**:
    - Run:
      ```bash
      ssh-keygen -t rsa -b 4096 -C "your_github_email@example.com"
      ```
    - When prompted, press Enter to save the key to the default location, and you can choose to set a passphrase or skip it (based on your preference).

2. **Copy SSH public key**:
    - On Windows, you can use the following command:
      ```bash
      type %userprofile%\.ssh\id_rsa.pub | clip
      ```
    - On *nix, just `cat` the file and copy the output for a similar outcome.

3. **Add key to GitHub**:
    - Go to GitHub and navigate to your profile.
    - Click on **Settings**.
    - In the left sidebar, select **SSH and GPG keys**.
    - Click **New SSH key**.
    - Provide a descriptive title (e.g., `Raygun4Java_<machine_name>`) and paste the copied key into the "Key" field.
    - Click **Add SSH key** to save.

4. **Test connection**:
   ```bash
   ssh -T git@github.com
   ```
    - If you see a message like "Hi [username]! You've successfully authenticated...", then the SSH key is set up correctly.

5. **On Windows, ensure the `ssh-agent` service is running**:
    - Press Windows + R to open the Run dialog.
    - Type `services.msc` and hit Enter.
    - Find **OpenSSH Authentication Agent** in the list.
    - Right-click on it and select Start.

6. **Add your SSH private key to the agent**:
   ```bash
   ssh-add <path_to_key>/id_rsa
   ```

## Release

1. **Prepare the release**:
    - Create a release branch off `master`. Do not use the protected branch `master`, the `maven-release-plugin` needs to make changes. 
    - Ensure all changes are committed to your branch.
    - Run the Maven `release:prepare` command:
      ```bash
      mvn release:prepare --projects .,core,webprovider,webproviderjakarta,playprovider
      ```
    - This will prepare the release for the projects we actually want to deploy to Mavencentral and will not release the sample apps.
    - You will be asked for the release version, release tag name, and next development version, which you can usually all leave
      as-is. Make sure the release version doesn't contain `-SNAPSHOT`. It will then make changes to the POMs and
      commit/tag them in Git.

2. **Deploy to Sonatype**:
    - After preparing, you can perform the release by running:
      ```bash
      mvn release:perform --projects .,core,webprovider,webproviderjakarta,playprovider
      ```
    - This command will check out the code from Git using the tag created in the prepare step, build the project, and
      deploy it to the OSSRH repository (Sonatype).

3. **Release the artifacts on Sonatype**:
    - Once the artifacts are uploaded to Sonatype, follow these steps to release them:
        - Locate our **Sonatype login** in 1Password.
        - Go to [Sonatype OSSRH](https://oss.sonatype.org/#stagingRepositories) and login.
        - Navigate to "Staging Repositories".
        - Find your uploaded artifact in the list.
        - Select your artifact, then click "Close". This will validate and prepare the artifact for release.
        - Once closed successfully, select the artifact again and click "Release" to make it publicly available.

4. **Verify on Maven Central**:
    - Your artifacts will be synchronized from OSSRH to Maven Central. This might take some time. You can periodically
      check [Maven Central Repository](https://search.maven.org/) to see if your library has been updated. Search for
      your library, e.g., "raygun4java", to verify.

*Remember:*

- If anything goes wrong during the release process, you can use the `mvn release:rollback` command to rollback any
  changes made by the `release:prepare` command.
