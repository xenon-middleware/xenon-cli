#!/usr/bin/env cwl-runner

# Examples:
# xenon --adaptor ssh --json list --location localhost /etc
# xenon --adaptor local --json list /etc

cwlVersion: v1.0
class: CommandLineTool
doc: List objects on remote storage
requirements:
- class: DockerRequirement
  # xenon-cli Docker container needs to be manually build before
  dockerImageId: nlesc/xenon-cli
arguments:
- --json
- list
inputs:
  adaptor:
    type: string
    inputBinding:
      prefix: --adaptor
      position: -1
  location:
    type: string?
    inputBinding:
      prefix: --location
      position: 1
  path:
    type: string
    inputBinding:
      position: 2
outputs:
  objects:
    type:
      type: array
      items: string
stdout: cwl.output.json