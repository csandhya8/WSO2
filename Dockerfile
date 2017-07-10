FROM java:8
VOLUME /tmp
EXPOSE 8080
ADD http://localhost:8077/nexus/content/repositories/releases/com/cadt/techm/sampledemo/2.0/sampledemo-2.0.war app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
