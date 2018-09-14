SET LOG_DIR=D:\logs
mvn -Dmaven.test.skip=true clean -Xpackage > %LOG_DIR%\compile-dvdtheque-swing.log 2>>&1