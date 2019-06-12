sudo systemctl stop dvdtheque-dev-rest.service

sudo cp dvdtheque-web/target/dvdtheque-web-*.jar /opt/dvdtheque_rest_service/dev/dvdtheque-web.jar

sudo systemctl start dvdtheque-dev-rest.service