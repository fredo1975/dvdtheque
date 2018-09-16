REM E:\dev\apache-tomcat-9.0.1\bin\shutdown.bat

del E:\dev\apache-tomcat-9.0.1\webapps\dvdtheque-web-1.0-SNAPSHOT.war
REM echo "del E:\dev\apache-tomcat-9.0.1\webapps\dvdtheque-web-1.0-SNAPSHOT.war"


del /f /s /q E:\dev\apache-tomcat-9.0.1\webapps\dvdtheque-web-1.0-SNAPSHOT 1>nul
REM echo "del /f /s /q E:\dev\apache-tomcat-9.0.1\webapps\dvdtheque-web-1.0-SNAPSHOT 1>nul"

rmdir /s /q E:\dev\apache-tomcat-9.0.1\webapps\dvdtheque-web-1.0-SNAPSHOT
REM echo "rmdir /s /q E:\dev\apache-tomcat-9.0.1\webapps\dvdtheque-web-1.0-SNAPSHOT"

xcopy E:\dev\github\dvdtheque\dvdtheque-web\target\dvdtheque-web-1.0-SNAPSHOT.war E:\dev\apache-tomcat-9.0.1\webapps
REM echo "xcopy E:\dev\github\dvdtheque\dvdtheque-web\target\del dvdtheque-web-1.0-SNAPSHOT.war E:\dev\apache-tomcat-9.0.1\webapps"

E:\dev\apache-tomcat-9.0.1\bin\startup-debug.bat