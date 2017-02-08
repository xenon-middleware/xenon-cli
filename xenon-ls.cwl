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
baseCommand: xenon
arguments:
- prefix: --format
  valueFrom: cwljson
  position: 0
- valueFrom: list
  position: 3
inputs:
  certfile:
    type: File?
    doc: Certificate file
    inputBinding:
      prefix: --certfile
      position: 0
  username:
    type: string?
    inputBinding:
      prefix: --username
      position: 0
  password:
    type: string?
    doc: Password, watch out do not use on systems with untrusted users
    inputBinding:
      prefix: --password
      position: 0
  scheme:
    type: string
    doc: Scheme, eg. file, sftp, ftp
    inputBinding:
      position: 1
  location:
    type: string?
    doc: List contents of path at location
    inputBinding:
      prefix: --location
      position: 2
# TODO prop should be optional, atm is must be set
#  prop:
#    doc: Xenon adaptor properties
#    type:
#      type: array
#      items: string
#      inputBinding:
#        prefix: --prop
#    inputBinding:
#      position: 2
  recursive:
    doc: List directories recursively
    type: boolean?
    inputBinding:
      prefix: --recursive
      position: 4
  path:
    type: string
    doc: List contents of path at location
    inputBinding:
      position: 5
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
