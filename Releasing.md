# Releasing

Travis CI is preconfigured to publish from artifacts automatically for you.
Whenever you push code to master, Travis CI will automatically build the artifacts (.jar, java doc, etc.), sign them and upload them to [OSS Sonatype](https://oss.sonatype.org).
This means, you have to do nothing special on your machine to publish a new version of TikXml, Travis CI will do all the hard work for you.

However, there are two kind of releases:
1. SNAPSHOT release
2. Stable release

All you have to do is to set `VERSION_NAME=x.y.z-SNAPSHOT` in `gradle.properties` and push it to master and a SNAPSHOT will be released automatically. No further action is required.
Having the SNAPSHOT suffix should be the default value for master branch unless you would like to publish a stable release.

If you would like to release a stable version you have to do the following:
1. Remove the SNAPSHOT suffix in `gradle.properties` such that VERSION_NAME looks like this `VERSION_NAME=x.y.z`
2. Commit and push this change to master.
3. Travis CI will automatically build the artifacts and upload them to [OSS Sonatype](https://oss.sonatype.org).
4. Log into [OSS Sonatype](https://oss.sonatype.org) with the tickaroo credentials (ask Hannes in case you don't know them).
5. Click to on the `Staging Repositories` link on the left (inside the Build Promotion block).
6. Scroll down the list until the end. There should be a `comtickaroo xyz` entry. Click on it
7. Check the checkbox in front of `comtickaroo xyz` and the click the `Close` button (on top of the screen).
8. Wait some time (you may have to press the `refresh button`).
9. If everything went well, then press `Release` button and Stable release will be released to Maven Central

That's it.
