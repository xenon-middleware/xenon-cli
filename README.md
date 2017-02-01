# Xenon Command Line Interface

# Build

```
./gradlew build
```

Generates application tar/zip in `build/distributions/` directory.

## Docker

To use Xenon CLI from a Docker image. 
The Docker image can be build with
```
./gradlew installDist
docker build -t nlesc/xenon-cli .
```

## Common Workflow Language

Run Xenon CLI using a cwl-runner or as tool in a Common Workflow Language workflow.

Requires `nlesc/xenon-cli` Docker image.

Example to list contents of `/etc` directory via a ssh to localhost connection with cwl-runner:
```
cwl-runner xenon-ls.cwl --adaptor local --location localhost --path /etc
```
