# Introduction

The `service` component implements an NLP application runner for text-processing and exposes a REST endpoint for communication.

For the moment, only a runner for [GATE NLP](https://gate.ac.uk/) (using GATE Embedded) applications has been implemented.


# Configuration

The application requires a configuration file, which specifies which NLP application should be run with additional parameters. 

## Service
The available properties with running the service are:
- `server.port` - the port number on which the Service will be listening (default: `8095`).

## NLP application
The available properties with running the NLP application will be exposed to the client and these are:
- `application.class.name` - the name of the application runner (for the moment, only GATE: `nlp.service.gate.service.GateNlpService`),
- `application.name` - the name of the application,
- `application.version` - application version,
- `application.language` - application language,
- `application.params` - NLP-application specific parameters.

In case of running a GATE Application, the available parameters are:
- `gateAppPath` - the path to the GATE application to be run (mandatory),
- `gateHome` - the path to GATE installation directory (mandatory),
- `annotationSets` - the annotations sets to be used (optional),
- `gateControllerNum` - the number of GATE controllers to be run in parallel (for multi-threading).

## Example

An example configuration file is provided in `src/main/resources/application.properties`

# API specification

The API specificaiton in [OpenAPI](https://www.openapis.org/) standard is provided in `api-specs` directory - please refer to: [openapi.yaml](./api-specs/openapi.yaml)


# Missing
- tests
