#!/usr/bin/env cwl-runner
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
  scheme:
    type: string
    inputBinding:
      prefix: --scheme
      position: -1
  certfile:
    type: File?
    inputBinding:
      prefix: --certfile
      position: -1
  location:
    type: string?
    inputBinding:
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
  files:
    type:
      type: array
      items: string
  directories:
    type:
      type: array
      items: string
stdout: cwl.output.json
