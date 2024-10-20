# Build stage
FROM eclipse-temurin:22.0.2_9-jdk AS builder

WORKDIR /root

COPY pom.xml ./
COPY ./.mvn /root/.mvn
COPY mvnw /root/mvnw

# Copy the project files
COPY app/pom.xml ./app/pom.xml
COPY auth/pom.xml ./auth/pom.xml
COPY blog/pom.xml ./blog/pom.xml
COPY core/pom.xml ./core/pom.xml
COPY discussions/pom.xml ./discussions/pom.xml
COPY education/pom.xml ./education/pom.xml
COPY engine/pom.xml ./engine/pom.xml
COPY media/pom.xml ./media/pom.xml
COPY payments/pom.xml ./payments/pom.xml
COPY user/pom.xml ./user/pom.xml
COPY chat/pom.xml ./chat/pom.xml
COPY lemon-squeezy/pom.xml ./lemon-squeezy/pom.xml

RUN apt-get update && apt-get install -y dos2unix && dos2unix /root/mvnw

# Download the dependencies
RUN chmod +x /root/mvnw
RUN bash ./mvnw dependency:go-offline

# Copy the project files
COPY app ./app
COPY auth ./auth
COPY blog ./blog
COPY core ./core
COPY discussions ./discussions
COPY education ./education
COPY engine ./engine
COPY media ./media
COPY payments ./payments
COPY user ./user
COPY chat ./chat
COPY lemon-squeezy ./lemon-squeezy

# Build the project
RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:22.0.2_9-jdk 

WORKDIR /root

COPY --from=builder /root/app/target/app-0.0.1-SNAPSHOT.jar /root/app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "/root/app.jar"]
