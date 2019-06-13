echo 'stoping dvdtheque-dev-rest.service ...'
sudo systemctl stop dvdtheque-dev-rest.service
echo 'copying dvdtheque-web-*.jar to  /opt/dvdtheque_rest_service/dev/dvdtheque-web.jar ...'
sudo cp dvdtheque-web/target/dvdtheque-web-*.jar /opt/dvdtheque_rest_service/dev/dvdtheque-web.jar
echo 'starting dvdtheque-dev-rest.service ...'
sudo systemctl start dvdtheque-dev-rest.service