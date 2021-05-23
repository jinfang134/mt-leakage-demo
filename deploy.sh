#!/bin/zsh

./gradlew clean build -x test

scp ./build/libs/mt-*.jar ubuntu@manage.daotonginfo.com:~/workspace/jmreport/jmreport.jar

#ssh -t ubuntu@manage.daotonginfo.com "sudo service daotong-medicine restart "