# Introduction

This project implements an NLP application runner for text-processing that exposes a REST endpoint for communication.

For the moment, only a runner for [GATE NLP](https://gate.ac.uk/) (using GATE Embedded) applications has been implemented.


# Configuration

The application requires a configuration file, which specifies which GATE NLP application should be run with additional parameters. 

## Service
The available properties with running the service are:
- `server.port` - the port number on which the Service will be listening (default: `8095`).
- `endpoint.single-doc.fail-on-empty-content` - whether to fail on receiving an empty document when processing single document (default: `false`).

## NLP application
The available properties with running the NLP application will be exposed to the client and these are:
- `application.name` - the name of the application,
- `application.version` - application version,
- `application.language` - application language,
- `application.params` - NLP-application specific parameters.

When providing a GATE application, some of the available parameters are:
- `gateAppPath` - the path to the GATE application to be run (mandatory),
- `gateHome` - the path to GATE installation directory (mandatory),
- `gateControllerNum` - the number of GATE controllers to be run in parallel (for multi-threading),
- `gateAnnotationSets` - the annotations sets to be used (optional).

## Example

An example configuration file is provided in `app/src/main/resources/application.properties`

# API specification

The API specificaiton in [OpenAPI](https://www.openapis.org/) standard is provided in `api-specs` directory - please refer to: [openapi.yaml](api-specs/openapi.yaml)
