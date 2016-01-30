# Tik XML
A fast xml parser for android (and java)

# Work in Progress
[![Build Status](https://travis-ci.org/Tickaroo/tikxml.svg?branch=master)](https://travis-ci.org/Tickaroo/tikxml)

The documentation can be found [here](https://github.com/Tickaroo/tikxml/blob/master/docs/AnnotatingModelClasses.md)

Latest snapshot `0.1-SNAPSHOT` available: 

```groovy
repositories {
  mavenCentral()
  maven {
    url 'http://oss.sonatype.org/content/repositories/snapshots'
  }
}
```

```groovy
compile 'com.tickaroo.tikxml:annotation:0.1-SNAPSHOT'
compile 'com.tickaroo.tikxml:core:0.1-SNAPSHOT'

apt 'com.tickaroo.tikxml:processor:0.1-SNAPSHOT'
```

For retrofit2-beta3:

```groovy
compile 'com.tickaroo.tikxml:retrofit-converter:0.1-SNAPSHOT'
```

# Benchmark
We did benchmark on this early version of TikXml to compare it's results with other popular xml parsers like SimpleXml and Jackson.
TikXml is working around 1,9 x faster than jackson and 4,3 times faster than SimpleXml by also having a low memory footprint:
![Benchmark](https://github.com/Tickaroo/tikxml/blob/master/docs/Benchmark.png")

TikXml has been build on top of Okio and therefore highly optimized for Retrofit2.

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