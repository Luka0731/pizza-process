# axon ivy engine + portal
ARG IVY_IMAGE=axonivy/axonivy-engine:nightly-14.0
FROM ${IVY_IMAGE} AS engine

# pizza process
FROM maven:3.9-eclipse-temurin-25 AS build
ARG PORTAL_VERSION=14.0.0
COPY --from=engine /ivy /opt/ivy-engine
COPY main/ /build/main/
RUN curl -fsSL -o /opt/lombok.jar https://repo1.maven.org/maven2/org/projectlombok/lombok/1.18.46/lombok-1.18.46.jar
ENV MAVEN_OPTS="-javaagent:/opt/lombok.jar=ECJ"
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -f /build/main/pom.xml package -Divy.engine.directory=/opt/ivy-engine -DskipTests
RUN mkdir /app && cp /build/main/target/*.iar /app/main.iar \
 && for a in portal portal-components; do \
    curl -fsSL -o /app/$a.iar https://maven.axonivy.com/com/axonivy/portal/$a/${PORTAL_VERSION}/$a-${PORTAL_VERSION}.iar; \
    done \
 && cd /app && jar --create --no-manifest --file /pizza.zip *.iar

# auto deploy
FROM ${IVY_IMAGE}
COPY --from=build --chown=1000:0 /pizza.zip /pizza/pizza.zip
RUN printf 'deployTestUsers: "TRUE"\n' > /pizza/pizza.zip.options.yaml
ENTRYPOINT ["/bin/sh", "-c", "mkdir -p /ivy/deploy && cp /pizza/pizza.zip.options.yaml /pizza/pizza.zip /ivy/deploy/ && exec /ivy/bin/AxonIvyEngine"]