# Xenon Command Line Interface

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
xenon --adaptor local --json list /etc
xenon --adaptor ssh --json list --location localhost /etc
xenon --adaptor ssh --json upload --source /etc/passwd --location localhost --path /tmp/copy-of-passwd 
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
./xenon-ls.cwl --scheme ssh --location <user>@<host> --path $PWD --certfile ~/.ssh/id_rsa
./xenon-upload.cwl --scheme ssh --source $PWD/README.md --location <user>@<host> --path /tmp/copy-of-README.md --certfile ~/.ssh/id_rsa
./xenon-download.cwl --scheme ssh --location <user>@<host> --path /tmp/copy-of-README.md --target /tmp/another-copy-of-README.md --certfile ~/.ssh/id_rsa
```
(Replace `<user>@<host>` with actual username and hostname)