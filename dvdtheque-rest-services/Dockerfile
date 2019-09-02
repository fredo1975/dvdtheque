# Base Alpine Linux based image with OpenJDK JRE only
FROM openjdk:8-jre-alpine
# copy application JAR (with libraries inside)
COPY target/dvdtheque-rest-services*.jar /dvdtheque-rest-services.jar
# specify default command
CMD ["/usr/bin/java","-jar", "/dvdtheque-rest-services.jar"]