FROM openjdk:8-jdk-alpine
COPY build/libs/*.jar tourGuide-1.0.0.jar
ENTRYPOINT ["java","-jar","/tourGuide-1.0.0.jar"]