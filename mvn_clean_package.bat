# add comment
# my comm from 192.168.1.26 
SET CONFIG=local
SET LOG_DIR=D:\logs
mvn -e -P%CONFIG% -Ddvdtheque.local.log.dir=%LOG_DIR% clean package > %LOG_DIR%\package-dvdtheque.%CONFIG%.log 2>>&1
