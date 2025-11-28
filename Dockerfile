###
# Image pour la compilation
FROM maven:3-eclipse-temurin-21 as build-image
WORKDIR /build/
# Installation et configuration de la locale FR
RUN apt update && DEBIAN_FRONTEND=noninteractive apt -y install locales
RUN sed -i '/fr_FR.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen
ENV LANG fr_FR.UTF-8
ENV LANGUAGE fr_FR:fr
ENV LC_ALL fr_FR.UTF-8


# On lance la compilation Java
# On débute par une mise en cache docker des dépendances Java
# cf https://www.baeldung.com/ops/docker-cache-maven-dependencies
COPY ./pom.xml /build/bacon-provider-api/pom.xml
RUN mvn -f /build/bacon-provider-api/pom.xml verify --fail-never
# et la compilation du code Java
COPY ./   /build/

RUN mvn --batch-mode \
        -Dmaven.test.skip=false \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package spring-boot:repackage

FROM maven:3-eclipse-temurin-21 as baconprovider-builder
WORKDIR application
COPY --from=build-image /build/target/bacon-provider-api.jar bacon-provider-api.jar
RUN java -Djarmode=layertools -jar bacon-provider-api.jar extract

###
# Image pour le module API
#FROM tomcat:9-jdk17 as api-image
#COPY --from=build-image /build/web/target/*.war /usr/local/tomcat/webapps/ROOT.war
#CMD [ "catalina.sh", "run" ]
FROM eclipse-temurin:21-jdk as bacon-provider-api-image
RUN apt-get update
RUN apt-get install -y locales locales-all
ENV LC_ALL fr_FR.UTF-8
ENV LANG fr_FR.UTF-8
ENV LANGUAGE fr_FR.UTF-8
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /app/

COPY --from=baconprovider-builder application/dependencies/ ./
COPY --from=baconprovider-builder application/spring-boot-loader/ ./
COPY --from=baconprovider-builder application/snapshot-dependencies/ ./
COPY --from=baconprovider-builder application/*.jar ./bacon-provider-api.jar

ENTRYPOINT ["java","-jar","/app/bacon-provider-api.jar"]
