FROM amazoncorretto:17-alpine-jdk

ENV EXPIRATION=${EXPIRATION}
ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

RUN apk add --no-cache maven

COPY . /app

WORKDIR /app


ENV SPRING_PROFILES_ACTIVE=docker

RUN mvn package -DskipTests=true;

ENTRYPOINT ["java", "-jar", "/app/target/task-management-system-0.0.1-SNAPSHOT.jar"]