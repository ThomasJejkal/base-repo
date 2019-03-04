#!/bin/bash

echo "Updating submodules"
git submodule foreach git pull origin master
echo "Building service-base library"
cd libraries/service-base 
./gradlew clean check install
cd ../../
cd libraries/generic-message-consumer
./gradlew clean check install
cd ../../
echo "Building project and executing tests"
./gradlew -Ptravis clean check jacocoTestReport
