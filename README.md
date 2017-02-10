# Xenon Command Line Interface

[![Build Status](https://travis-ci.org/NLeSC/xenon-cli.svg?branch=master)](https://travis-ci.org/NLeSC/xenon-cli)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5956168fece74be4af782398a3310f65)](https://www.codacy.com/app/NLeSC/xenon-cli?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=NLeSC/xenon-cli&amp;utm_campaign=Badge_Grade)

Command line interface which uses the [Xenon library](https://nlesc.github.io/Xenon) to perform job and file operations.

# Install

For now it must be build, see [Build chapter](#build) below.
The distribution can be installed with:
```
tar -xf build/distributions/xenon*.tar
xenon*/bin/xenon --help
```
Add `xenon*/bin` to your PATH environment variable for easy usage.

# Usage

```
xenon file list /etc
xenon sftp --location localhost list /etc
xenon sftp --location localhost upload /etc/passwd /tmp/copy-of-passwd
xenon ssh --location localhost exec /bin/hostname
echo "sleep 30;echo Hello" | xenon sftp --location localhost upload - /tmp/myjob.sh
xenon slurm --location localhost submit /bin/sh /tmp/myjob.sh
```

To keep password or passphrase invisible in process list put the password in a text file (eg. 'password.txt') and then use '@password.txt' as argument.
For example:
```
xenon --username $USER --password @password.txt sftp --location localhost list $PWD/src
```

# Build

```
./gradlew build
```

Generates application tar/zip in `build/distributions/` directory.

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

Run Xenon CLI using a cwl-runner or as a tool in a Common Workflow Language workflow.

Requires `nlesc/xenon-cli` Docker image to be available locally.

Example to list contents of `/etc` directory via a ssh to localhost connection with cwl-runner:
```
./xenon-ls.cwl --scheme sftp --location $USER@172.17.0.1 --path $PWD --certfile ~/.ssh/id_rsa
./xenon-upload.cwl --certfile ~/.ssh/id_rsa --scheme sftp --location $USER@172.17.0.1 --source README.md --target $PWD/copy-of-README.md
./xenon-download.cwl --certfile ~/.ssh/id_rsa --scheme sftp --location $USER@172.17.0.1 --source $PWD/README.md --target copy-of-README.md
```
(Replace `<user>@<host>` with actual username and hostname + expects docker with default network range)
