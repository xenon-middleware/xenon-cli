Conda recipe or xenon command line interface.

# Build

```sh
conda install conda-build
conda-build -c conda-forge xenon-cli
```

# Upload

```sh
conda install anaconda-client
anaconda upload <path to xenon-cli*.tar.bz2>
```

# Install

```sh
conda install -c nlesc xenon-cli
```
