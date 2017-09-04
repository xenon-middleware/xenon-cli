#!/usr/bin/env cwl-runner
# Native:
# xenon --format cwljson sftp --location localhost list $PWD/
# Using cwl-runnner:
# ./xenon-ls.cwl --scheme sftp --location $USER@172.17.0.1 --path $PWD --certfile ~/.ssh/id_rsa
cwlVersion: v1.0
class: CommandLineTool
doc: List objects on remote storage
hints:
- class: DockerRequirement
  # xenon-cli Docker container needs to be manually build before
  dockerImageId: nlesc/xenon-cli
baseCommand:
- xenon
- '--json'
arguments:
- valueFrom: filesystem
  position: 1
- valueFrom: list
  position: 4
inputs:
  certfile:
    type: File?
    doc: Certificate file
    inputBinding:
      prefix: --certfile
      position: 3
  username:
    type: string?
    inputBinding:
      prefix: --username
      position: 3
  password:
    type: string?
    doc: Password, watch out do not use on systems with untrusted users
    inputBinding:
      prefix: --password
      position: 3
  adaptor:
    type: string
    doc: Adaptor name, eg. file, sftp, ftp
    inputBinding:
      position: 2
  location:
    type: string?
    doc: List contents of path at location
    inputBinding:
      prefix: --location
      position: 3
# TODO prop should be optional, atm is must be set
#  prop:
#    doc: Xenon adaptor properties
#    type:
#      type: array
#      items: string
#      inputBinding:
#        prefix: --prop
#    inputBinding:
#      position: 3
  recursive:
    doc: List directories recursively
    type: boolean?
    inputBinding:
      prefix: --recursive
      position: 5
  hidden:
    doc: Include hidden files/directories
    type: boolean?
    inputBinding:
      prefix: --hidden
      position: 5
  path:
    type: string
    doc: List contents of path at location
    inputBinding:
      position: 6
outputs:
  files:
    type:
      type: array
      items: string
stdout: cwl.output.json
