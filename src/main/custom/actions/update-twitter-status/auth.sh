#!/bin/bash

# Shell script to start Twitter authentication process

java -Dlog4j.configuration=file:../../conf/log4j.xml -jar ../../lib/TwitterAlert.jar auth
