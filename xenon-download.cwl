#!/usr/bin/env cwl-runner
cwlVersion: v1.0
class: CommandLineTool
doc: Download file from remote storage
requirements:
- class: DockerRequirement
  # xenon-cli Docker container needs to be manually build before
  dockerImageId: nlesc/xenon-cli
arguments:
- --json
- download
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
  target:
    type: string
    inputBinding:
      position: 3
outputs:
  target:
    type: File
    outputBinding:
      glob: $(inputs.target)
# stdout: cwl.output.json
