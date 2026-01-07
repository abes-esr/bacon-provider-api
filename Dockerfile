###
# Image pour la compilation
FROM maven:3-eclipse-temurin-21 AS build-image
WORKDIR /build/
# Installation et configuration de la locale FR
RUN apt update && DEBIAN_FRONTEND=noninteractive apt -y install locales
RUN sed -i '/fr_FR.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen
ENV LANG=fr_FR.UTF-8
ENV LANGUAGE=fr_FR:fr
ENV LC_ALL=fr_FR.UTF-8


# On lance la compilation Java
# On débute par une mise en cache docker des dépendances Java
# cf https://www.baeldung.com/ops/docker-cache-maven-dependencies
COPY ./pom.xml /build/bacon-provider-api/pom.xml
RUN mvn verify --fail-never
# et la compilation du code Java
COPY ./   /build/

RUN mvn --batch-mode \
        -Dmaven.test.skip=false \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package spring-boot:repackage

FROM maven:3-eclipse-temurin-21 AS baconprovider-builder
WORKDIR /application
COPY --from=build-image /build/target/bacon-provider-api.jar bacon-provider-api.jar
RUN java -Djarmode=layertools -jar bacon-provider-api.jar extract


FROM ossyupiik/java:21.0.8 AS bacon-provider-api-image
WORKDIR /app/
COPY --from=baconprovider-builder /application/dependencies/ ./
COPY --from=baconprovider-builder /application/spring-boot-loader/ ./
COPY --from=baconprovider-builder /application/snapshot-dependencies/ ./
COPY --from=baconprovider-builder /application/*.jar ./bacon-provider-api.jar

ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD ["java","-jar","/bacon-provider-api.jar"]
