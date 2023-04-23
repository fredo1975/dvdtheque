# dvdtheque-rest-service

local start-up cmd
-Dspring.profiles.active=local1 -Dspring.cloud.config.label=develop

dev start-up cmd
sudo groupadd -r java-app-gr

sudo useradd -r -s /bin/false -g java-app-gr dvdtheque-user

sudo nano /etc/systemd/system/dvdtheque-rest.service


[Unit]
Description=Manage Dvdtheque Development env Java Rest service

[Service]
WorkingDirectory=/opt/dvdtheque_rest_service
ExecStart=/usr/lib/jvm/java-11-openjdk-armhf/bin/java -Xms64M -Xmx512M -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8001,suspend=n -Djava.io.tmpdir=/opt/dvdtheque_rest_service/tmpFileDir -jar dvdtheque-rest-services.jar --spring.profiles.active=dev2 --spring.cloud.config.uri=http://192.168.1.103:8888,http://192.168.1.105:8888 --spring.cloud.config.label=develop --add-modules java.se --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED
User=dvdtheque-user
Type=simple
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target


sudo mkdir /opt/dvdtheque_rest_service

sudo mkdir /opt/dvdtheque_rest_service/tmpFileDir

sudo chown -R dvdtheque-user:java-app-gr /opt/dvdtheque_rest_service

sudo chown -R dvdtheque-user:java-app-gr /opt/dvdtheque_rest_service/tmpFileDir

sudo chmod -R g+w /opt/dvdtheque_rest_service

sudo systemctl daemon-reload

sudo systemctl start dvdtheque-dev-rest.service

sudo systemctl status dvdtheque-dev-rest.service