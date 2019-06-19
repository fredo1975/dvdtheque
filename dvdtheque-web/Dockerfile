# Base Alpine Linux based image with OpenJDK JRE only
FROM openjdk:8-jre-alpine
# copy application JAR (with libraries inside)
COPY target/dvdtheque-web-2.5.0.jar /dvdtheque-web.jar
# specify default command
CMD ["/usr/bin/java", "-jar", "/dvdtheque-web.jar --spring.profiles.active=prod"]