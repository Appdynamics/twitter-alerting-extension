@echo off
rem ------------------------------------------------------------
rem Batch file to send notification as Twitter status
rem ------------------------------------------------------------
call consumer.conf
java -Dlog4j.configuration=file:..\..\conf\log4j.xml -Dconsumer.key=%CONSUMER_KEY% -Dconsumer.secret=%CONSUMER_SECRET% -jar ..\..\lib\TwitterAlert.jar %*
