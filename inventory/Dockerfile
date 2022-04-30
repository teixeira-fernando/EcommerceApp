FROM adoptopenjdk/openjdk11:alpine-slim
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
CMD ["./mvnw", "spring-boot:run"]


#FROM adoptopenjdk/openjdk11:alpine-slim
#COPY target/inventory-0.0.1-SNAPSHOT.jar /inventory.jar
#ENTRYPOINT ["java","-jar","/inventory.jar"]