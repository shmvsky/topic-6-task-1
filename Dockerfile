FROM openjdk:17-alpine as build
WORKDIR /app
COPY . .
RUN /app/gradlew clean build -x test --no-daemon


FROM bellsoft/liberica-openjre-alpine-musl:17
ARG DEPENDENCY=/app/build
COPY --from=build ${DEPENDENCY}/libs /app/lib
RUN mv /app/lib/*SNAPSHOT.jar /app/lib/app.jar
ENTRYPOINT ["java","-jar","app/lib/app.jar"]