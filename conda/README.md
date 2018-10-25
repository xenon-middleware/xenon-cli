Conda recipe or xenon command line interface.

# Build

Update version number in xenon-cli/meta.yaml.

```sh
./gradlew installShadowDist
cd conda
conda install conda-build
conda-build -c conda-forge xenon-cli
```

# Upload

```sh
conda install anaconda-client
anaconda upload --user nlesc <path to xenon-cli*.tar.bz2>
```

# Install

```sh
conda install -c nlesc xenon-cli
```
