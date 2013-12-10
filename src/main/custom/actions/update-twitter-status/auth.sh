#!/bin/bash

# Shell script to start Twitter authentication process

. consumer.conf
java -Dlog4j.configuration=file:../../conf/log4j.xml -Dconsumer.key=$CONSUMER_KEY -Dconsumer.secret=$CONSUMER_SECRET -jar ../../lib/TwitterAlert.jar auth
