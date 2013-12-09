@echo off
rem ------------------------------------------------------------
rem Batch file to send notification as Twitter status
rem ------------------------------------------------------------
java -Dlog4j.configuration=file:..\..\conf\log4j.xml -jar ..\..\lib\TwitterAlert.jar %*
