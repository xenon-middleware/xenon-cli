# Xenon Command Line Interface

[![Build Status](https://travis-ci.org/xenon-middleware/xenon-cli.svg?branch=master)](https://travis-ci.org/xenon-middleware/xenon-cli)
[![Build status](https://ci.appveyor.com/api/projects/status/vki0xma8y7glpt09/branch/master?svg=true)](https://ci.appveyor.com/project/xenon-middleware/xenon-cli/branch/master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xenon-middleware_xenon-cli&metric=alert_status)](https://sonarcloud.io/dashboard?id=xenon-middleware_xenon-cli)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=xenon-middleware_xenon-cli&metric=coverage)](https://sonarcloud.io/dashboard?id=xenon-middleware_xenon-cli)
[![DOI](https://zenodo.org/badge/80642209.svg)](https://zenodo.org/badge/latestdoi/80642209)
[![Anaconda-Server Badge](https://anaconda.org/nlesc/xenon-cli/badges/installer/conda.svg)](https://anaconda.org/NLeSC/xenon-cli)

Command line interface which uses the [Xenon library](https://xenon-middleware.github.io/xenon) to perform job and file operations.

# Install

Goto https://github.com/xenon-middleware/xenon-cli/releases and download a tarball (or zipfile).
The tarball can be installed with:
```bash
tar -xf build/distributions/xenon*.tar
xenon*/bin/xenon --help
```
Add `xenon*/bin` to your PATH environment variable for easy usage.

# Usage

```bash
# List files on local filesystem
xenon filesystem file list /etc
# List files on remote filesystem using sftp
xenon filesystem sftp --location localhost list /etc
# Copy local file to remote filesystem
xenon filesystem sftp --location localhost upload /etc/passwd /tmp/copy-of-passwd
# Execute a program remotely using ssh
xenon scheduler ssh --location localhost exec /bin/hostname
# Pipe to a remote file
echo "sleep 30;echo Hello" | xenon sftp --location localhost upload - /tmp/myjob.sh
# Submit to a remote Slurm batch scheduler
xenon scheduler slurm --location ssh://localhost submit /bin/sh /tmp/myjob.sh
```

The above commands use your current username and keys from ~/.ssh.

To keep password or passphrase invisible in process list put the password in a text file (eg. 'password.txt') and then use '@password.txt' as argument.
For example:
```
xenon filesystem sftp --location localhost --username $USER --password @password.txt list $PWD/src
```

# Build

```
./gradlew build
```

Generates application tar/zip in `build/distributions/` directory.

# Tests

Requirements for the integration tests:
* [docker](https://docs.docker.com/engine/installation/), v1.13 or greater
* [docker-compose](https://docs.docker.com/compose/), v1.10 or greater

The unit and integration tests can be run with:
```
./gradlew check
```

# Release

1. Bump version in `build.gradle`, `conda/xenon-cli/meta.yaml` files, add version to `CHANGELOG.md` and commit/push
2. Run `./gradlew build` to build distributions
3. Create a new GitHub release
4. Upload the files in `build/distributions/` directory to that release
5. Publish release
6. Create conda release, see [conda/README.md](conda/README.md)

## Docker

Run Xenon CLI as a Docker container.

The Docker image can be build with
```
./gradlew docker
```

Generates a `nlesc/xenon-cli` Docker image.

To use local files use volume mounting (watch out as the path should be relative to mount point):
```
docker run -ti --rm nlesc/xenon-cli --user $USER -v $PWD:/work --adaptor ssh upload --source /work/somefile.txt --location localhost --path /tmp/copy-of-somefile.txt
```

## Common Workflow Language

Run Xenon CLI using a cwl-runner or as a tool in a [Common Workflow Language](http://www.commonwl.org/) workflow.

Requires `nlesc/xenon-cli` Docker image to be available locally.

Example to list contents of `/etc` directory via a ssh to localhost connection with cwl-runner:
```
./xenon-ls.cwl --adaptor sftp --location $USER@172.17.0.1 --certfile ~/.ssh/id_rsa --path /etc
# Copy file from localhost to working directory inside Docker container
./xenon-upload.cwl --adaptor sftp --certfile ~/.ssh/id_rsa --location $USER@172.17.0.1 --source $PWD/README.md --target /tmp/copy-of-README.md
# Copy file inside Docker container to localhost
./xenon-download.cwl --adaptor sftp --certfile ~/.ssh/id_rsa --location $USER@172.17.0.1 --source /etc/passwd --target $PWD/copy-of-passwd
```
(Replace `<user>@<host>` with actual username and hostname + expects docker with default network range)
