rem --------------------------------------------------
rem Batch file to start Twitter authentication process
rem --------------------------------------------------
java -Dlog4j.configuration=file:..\..\conf\log4j.xml -jar ..\..\lib\TwitterAlert.jar auth
