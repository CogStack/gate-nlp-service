# Introduction
This project implements the NLP as a service behind a REST API using [Spring Boot](https://spring.io/). The general idea is to be able send the text to NLP service and receive back the annotations.


## Project contents

This project consists of following packages: `common`, `service` and `gateway`.

### Common
 `common` package contains the common data model shared beteeen the `service` and `gateway` packages.
  
### Service
`service` package contains an exemplar implementation of a NLP service that expose REST API for processing the documents. 

For the moment, as an exemplar implementation, it only provides a wrapper around [GATE Embedded](https://gate.ac.uk/family/embedded.html) that can run any GATE application.

### Gateway
`gateway` package contains the Gateway implementation that will serve as a middle man between the actual NLP service and the client. It handles the communication between the services and exposes a uniform API.

For the moment, the Gateway serves only as a proxy between the internal NLP services and the client, and exposes the REST API compatible with the internal NLP services. 

In the next step, ther Gateway should expose the API compatible with [NLPRP REST API](https://crateanon.readthedocs.io/en/latest/nlp/nlprp.html) and allow for handling multiple NLP services with parallel processing and queueing mechanisms.


# Building

The detailed instructions on building individual applications are provided in their respecive package directories.


# Usage

For the moment, for the ease of use, please use the individual NLP service wrapper as a standalone service. The detailed instructions on it's use are available in `service` directory.


# Missing
- tests
