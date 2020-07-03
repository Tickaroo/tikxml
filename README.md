# Work in Progress
Please note that this is still **work in progress!**, although quite stable and used in production.
[![CircleCI](https://circleci.com/gh/Tickaroo/tikxml/tree/master.svg?style=svg)](https://circleci.com/gh/Tickaroo/tikxml/tree/master)

# TikXML
A fast xml parser for android (and java)

```groovy
implementation 'com.tickaroo.tikxml:annotation:0.8.15'
implementation 'com.tickaroo.tikxml:core:0.8.15'

annotationProcessor 'com.tickaroo.tikxml:processor:0.8.15'
```
For pure java project use this [apt plugin](https://github.com/tbroyer/gradle-apt-plugin) 

For koltin project (android or pure) use [kotlin-kapt plugin](https://kotlinlang.org/docs/reference/kapt.html) and use `kapt` instead of `annotationProcessor` in dependencies section of `build.gradle`.

(NOTE: In IDEA for non-android project this won't run annotation processor if you didn't set project to delegate build to gradle)

For retrofit2:

```groovy
implementation 'com.tickaroo.tikxml:retrofit-converter:0.8.15'
```

Also, an [AutoValue](https://github.com/google/auto/tree/master/value) extension is available:

```groovy
annotationProcessor 'com.tickaroo.tikxml:auto-value-tikxml:0.8.15'
```

Latest snapshot `0.9.0_11-SNAPSHOT` available:

```groovy
repositories {
  mavenCentral()
  maven {
    url 'http://oss.sonatype.org/content/repositories/snapshots'
  }
}
```

# Documentation
The **documentation** (<= 0.8.x) can be found [here](https://github.com/Tickaroo/tikxml/blob/master/docs/AnnotatingModelClasses.md)

The **documentation** (>= 0.9.x) can be found [here](https://github.com/Tickaroo/tikxml/blob/master/docs/index.md)

# Benchmark
We did benchmark on this early version of TikXml to compare field's results with other popular xml parsers like SimpleXml and Jackson.
TikXml is working around 1,9 times faster than jackson and 4,3 times faster than SimpleXml by also having a low memory footprint:
![Benchmark](https://raw.githubusercontent.com/Tickaroo/tikxml/master/docs/Benchmark.png)

TikXml has been built on top of Okio and therefore is highly optimized for Retrofit2.

# License

```
Copyright 2015 Tickaroo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


#### Releasing on Maven Central

If you are a Tickaroo employee and you want to release a new version on Maven Central, 
take a look [at this document](https://github.com/Tickaroo/tikxml/blob/master/Releasing.md)
