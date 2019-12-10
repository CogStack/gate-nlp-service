#!/bin/bash

java -Dspring.config.location=/app/nlp-service/config/ -Dspring.main.web-application-type=none -jar /app/nlp-service/app-*.jar