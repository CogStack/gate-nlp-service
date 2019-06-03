#!/bin/bash

# Build locally the image for BioYodie REST Service image
#
# Please note that during the image build process the image will try to automatically self-initialize
# and download all the necessary GATE plugins from Maven repository
#
# The build process requires that the UMLS resources are locally available at path:
#     ./applications/bio-yodie/bio-yodie-resources

UMLS_PATH="./bio-yodie/bio-yodie-resources"
if [ ! -e $UMLS_PATH ]; then
	echo "Cannot find BioYodie UMLS resources under: $UMLS_PATH"
	exit 1
fi

docker build -t gate-nlp-service-bioyodie -f bio-yodie/docker/Dockerfile .
