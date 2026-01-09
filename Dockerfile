###
# Image pour la compilation
FROM maven:3-eclipse-temurin-21 AS build-image
WORKDIR /build/

# On lance la compilation Java
# On débute par une mise en cache docker des dépendances Java
# cf https://www.baeldung.com/ops/docker-cache-maven-dependencies
COPY ./pom.xml /build/bacon-provider-api/pom.xml
RUN mvn verify --fail-never
# et la compilation du code Java
COPY ./   /build/

RUN mvn --batch-mode \
        -Dmaven.test.skip=true \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package -Passembly

FROM ossyupiik/java:21.0.8 AS bacon-provider-api-image
WORKDIR /
COPY --from=build-image /build/target/bacon-provider-api-distribution.tar.gz /
RUN tar xvfz bacon-provider-api-distribution.tar.gz
RUN rm -f /bacon-provider-api-distribution.tar.gz

ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD ["java","-cp", "/bacon/lib/*","fr.abes.baconprovider.BaconProviderApplication"]
