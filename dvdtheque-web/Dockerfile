# Base Alpine Linux based image with OpenJDK JRE only
FROM openjdk:8-jre-alpine
# copy application WAR (with libraries inside)
COPY target/dvdtheque-web-2.2.2-SNAPSHOT.jar /dvdtheque-web.jar
# specify default command
CMD ["/usr/bin/java", "-jar", "/dvdtheque-web.jar"]