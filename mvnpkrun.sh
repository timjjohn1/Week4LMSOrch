#!/bin/bash

mvn clean package
java -jar ./target/lmsorch-0.0.1-SNAPSHOT.jar 

