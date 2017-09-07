# Releasing

## Version

- Ensure that the public / private key pair to sign the artifacts are installed on your local machine. Ask Hannes or Andi where to get it from.
- From command line run the following commands.
  ````
  mvn clean source:jar javadoc:jar  dokka:javadocJar deploy --settings="releaseSettings.xml" -Dtickaroo.password=secretPassword -Dgpg.passphrase=secredPassphraseOfSigningKey
  ````
-  Login into [https://oss.sonatype.org/](https://oss.sonatype.org/) with tickaroo account (username=tickaroo, password same as in -Dtickaroo.password=secretPassword), then go into "Staging Repositories" and close and release the repository to finally publish the artifacts on maven central.
 
  
## Snapshots
Travis CI is already configured to publish snapshots of every git commit / push to master branch.
Note that for snapshot, signing of artifacts is not required. 