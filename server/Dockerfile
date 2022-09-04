FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
#ENV TZ="Europe/Moscow"
COPY /target/*exec.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]