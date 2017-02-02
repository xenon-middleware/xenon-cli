# Docker image can not be automated on Docker Hub because it needs compiled jar files
# It could be automated build, but the compilation will need to done inside the Dockerfile
# Using jdk will create a 941Mb image, while using a jre results in a 124Mb image, so choose to go with the jre route
# Using `./gradlew docker` to build image
FROM openjdk:jre-alpine
MAINTAINER Stefan Verhoeven <s.verhoeven@esciencecenter.nl>
COPY bin /app/bin
COPY lib /app/lib
WORKDIR /app
ENTRYPOINT ["/app/bin/xenon"]
CMD ["--help"]
