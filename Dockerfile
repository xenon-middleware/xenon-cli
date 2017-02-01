FROM openjdk:jre-alpine
MAINTAINER Stefan Verhoeven <s.verhoeven@esciencecenter.nl>
COPY build/install/xenon /app
WORKDIR /app
ENTRYPOINT ["/app/bin/xenon"]
CMD ["--help"]

