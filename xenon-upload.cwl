#!/usr/bin/env cwl-runner
cwlVersion: v1.0
class: CommandLineTool
doc: Upload file to remote storage
requirements:
- class: DockerRequirement
  # xenon-cli Docker container needs to be manually build before
  dockerImageId: nlesc/xenon-cli
arguments:
- --json
- upload
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
  source:
    type: File
    inputBinding:
      position: 1
  location:
    type: string?
    inputBinding:
      position: 2
  path:
    type: string
    inputBinding:
      position: 3
outputs:
  path: File
stdout: cwl.output.json
