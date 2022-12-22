# Archived
This project is archived and no longer maintained. For NLP services in CogStack deployments, consider using [MedCAT](https://github.com/CogStack/MedCAT) within [CogStack-Nifi](https://github.com/CogStack/CogStack-NiFi/).


# Introduction

This project implements a [GATE NLP](https://gate.ac.uk/) application runner for text-processing that exposes a REST API for communication. The general idea is to be able send a text to NLP service and receive back the annotations. It uses [GATE Embedded](https://gate.ac.uk/family/embedded.html) to run any GATE application and uses [Spring Boot](https://spring.io/projects/spring-boot) implementing the web service.


# API specification

Currently, there are 3 endpoints defined, that consume and return data in JSON format:
- *GET* `/api/info` - displays general information about the used GATE application (as provided in configuration file),
- *POST* `/api/process` - processes the provided documents and returns back the annotations,
- *POST* `/api/process_bulk` - processes the provided list of documents and returns back the annotations.

The full specification is available is [OpenAPI](https://www.openapis.org/) standard with the specification available in `api-specs` directory.


# Building
To build the GATE Service application, run in the main directory:

`./gradlew build`

The application build artifacts will be placed in `app/build/libs` directory.

During the build, the tests will be run, where the failed tests can also signify missing third-party dependencies (see below). 

However, to skip running the tests and just build the application, one can run:

`./gradlew bootJar`.

## Tests
To run the available tests, type:

`./gradlew test`


# Running the service application
The application can be either run as a standalone Java application or inside a Docker container. An example application configuration has been provided in `app/src/resources/application.yaml` file. The configuration file will include all the parameters required to run the service application with the specific GATE NLP application. The default version of configuration file is embeded in the jar file, but can be specified manually (see below).

Please note that the recommended way is to use the provided Docker image since a number of dependencies, such as the GATE NLP framework, need to be satisfied on a local machine.

## Running as a standalone Java application
Assuming that the build went correctly, to run the GATE NLP service application on the local machine:

`java [--Dspring.config.location=<service-config-dir>] -jar app/build/libs/app-*.jar`

where `<service-config-dir>` is the directory with a custom `application.properties` configuration file.

The running service will be listening on port `8095` (by default) on the host machine. 

## Using the Docker image
The latest stable Docker image is available in the Docker Hub under `cogstacksystems/nlp-rest-service-gate:latest` tag. Alternatively, the latest development version is available under `cogstacksystems/nlp-rest-service-gate:dev-latest` tag. The image can be also build locally using the provided `Dockerfile`.

To run GATE NLP Service container:

```
docker run -p 8095:8095 \
-v <service-config-dir>:/app/nlp-service/config \
-v <gate-app-dir>:/gate/app/ \
cogstacksystems/nlp-rest-service-gate:latest
```
where `<service-config-dir>` is the path to the directory a custom `application.properties` configuration file and `<gate-app-dir>` is the path to the directory containing GATE NLP application as specified in the provided configuration file. The service will be listening on port `8095` on the host machine.

## Example use
Assuming that the application is running on the `localhost` with the API exposed on port `8095`, one can run:
```
curl -XPOST http://localhost:8095/api/process \
  -H 'Content-Type: application/json' \
  -d '{"content":{"text":"The patient was prescribed with Aspirin."}}'
```

and the received result:
```
{
  "result": {
    "text": "The patient was prescribed with Aspirin.",
    "annotations": [
      {
        "end_idx": 39,
        "majorType": "Drug",
        "set": "",
        "name": "ASPIRIN",
        "start_idx": 32,
        "language": "",
        "id": 12,
        "minorType": "ActiveComponent",
        "text": "Aspirin",
        "type": "Drug"
      },
      {
        "end_idx": 39,
        "majorType": "Drug",
        "set": "",
        "name": "ASPIRIN",
        "start_idx": 32,
        "language": "",
        "id": 13,
        "minorType": "Medication",
        "text": "Aspirin",
        "type": "Drug"
      }
    ],
    "metadata": {
      "document_features": {
        "gate.SourceURL": "created from String"
      }
    },
    "success": true,
    "timestamp": "2019-12-04T09:51:32.246Z"
  }
}
```
Please note that the returned NLP annotations will depend on the underlying GATE NLP application used. As an example use we only provide a very basic drug annotation application build using GATE ANNIE Gazetteer. It uses as an input the data downloaded from [Drugs@FDA database](https://www.accessdata.fda.gov/scripts/cder/daf/) and further refined giving a curated list of drugs and active ingredients. 

GATE NLP applications and models utilising [SNOMED CT](https://www.england.nhs.uk/digitaltechnology/digital-primary-care/snomed-ct/) or [UMLS](https://www.nlm.nih.gov/research/umls/index.html), may require applying for licenses from the copyright holders. Please see: [Bio-YODIE](https://github.com/GateNLP/Bio-YODIE) as one of the applications for biomedical NER+L using UMLS.


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
- `gateControllerNum` - the number of GATE controllers that can be run in parallel, used for multi-threading (default: `1`),
- `gateAnnotationSets` - the annotations sets to be used (optional, default: `*`),
- `gateIncludeAnnotationText` - whether to include the `text` field with the annotation text (optional, default: `false`).

An example configuration file is provided in `app/src/main/resources/application.properties`
