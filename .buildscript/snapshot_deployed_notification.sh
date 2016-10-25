#!/usr/bin/env bash

# export POM_VERSION=`mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec`
#VERSION=`mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec`

#curl --header  "Authorization: Bearer xxxxx" -H "Content-Type: application/json" -X POST -d "{\"color\": \"green\", \"message\": \"New version of TikXml released: $VERSION\", \"notify\": false, \"message_format\": \"text\"}" https://api.hipchat.com/v2/room/Android-Devs/notification
