# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## Unreleased

### Added

* --temp-space argument ([#61](https://github.com/xenon-middleware/xenon-cli/issues/61))
* [at](https://linux.die.net/man/1/at) scheduler 

### Changed

* submit and exec sub command use a tasks+cores+nodes arguments, instead of nodes+processes+thread ([#625](https://github.com/xenon-middleware/xenon/issues/625)).
* Require Java 11 or greater, as enon package has same compatibility
* Upgraded to Xenon 3.0.0
* Switched to [testcontainers](https://www.testcontainers.org/) for testing against Docker containers

### Removed

* hdfs filesystem

## [2.4.1] - 2019-02-26

### Fixed

 * s3 adaptor gives wrong error due to gson conflict

## [2.4.0] - 2018-03-14

### Added

* scheduler arguments for exec and submit

### Changed

* Upgraded to Xenon 2.6.0

## [2.3.0] - 2018-03-05

### Added

* KeytabCredential support (#46)

### Changed

* Upgraded to Xenon 2.5.0

### Fixed

* Only show credentials flags supported by adaptor (#32)

## [2.2.1] - 2018-02-28

### Changed

* Upgraded to Xenon 2.4.1

### Fixed

* slf4j multiple bindings warning
* Slurm maxtime for interactive job does not appear functional (#29)
* On InvalidLocationException return supported locations (#31)
* On UnknownPropertyException return supported props (#34)

## [2.2.0] - 2018-02-26

### Added

* --name to to submit sub commands (#59)
* --max-memory to exec and submit sub command (#58)

### Changed

* Upgraded to Xenon 2.4.0

## [2.1.0] - 2018-02-16

### Added

* --start-single-process to exec and submit sub commands (#56)
* --inherit-env to exec and submit sub commands (#55)

### Changed

* Upgraded to Xenon 2.3.2-nohadoop

## [2.0.1] - 2018-01-30

### Changed

* Upgraded to Xenon 2.3.0-nohadoop

## [2.0.0] - 2017-11-07

### Added

* Subcommands
  * mkdir
  * rename
  * wait
* Status details to jobs list
* --long format for files list (#16)
* --verbose and --stacktrace arguments
* In `xenon --help`, added type column to list of adaptors

### Changed

* Upgraded to Xenon 2.2.0
* Renamed `--format cwljson` argument to `--json`
* Xenon CLI now has same major version as Xenon

## [1.0.3] - 2017-07-20

### Fixed

* Filter scheme properties based on XenonPropertyDescription.Component.SCHEDULER or XenonPropertyDescription.Component.FILESYSTEM (#12)

## [1.0.2] - 2017-05-08

### Changed

* Upgraded to Xenon 1.2.2

## Fixed

* Weird behavior with sftp (#10)

## [1.0.1] - 2017-03-02

### Changed

* Upgraded to Xenon 1.2.1

## [1.0.0] - 2017-02-23

Initial release
