################################
#
# Bio-Yodie builder
#
FROM cogstacksystems/nlp-rest-service-gate:dev-latest AS bioyodie-builder

RUN apt-get update && \
	apt-get install -y git ant && \
	apt-get clean autoclean && \
	apt-get autoremove -y && \
	rm -rf /var/lib/apt/lists/*

# clone the bio-yodie repo
WORKDIR /build/
RUN git clone https://github.com/GateNLP/Bio-YODIE.git

WORKDIR /build/Bio-YODIE
RUN git pull --recurse-submodules=on-demand && \
	git submodule update --init --recursive

# build plugins
WORKDIR /build/Bio-YODIE/plugins
RUN bash compilePlugins.sh



################################
#
# GATE Service Runner with ANNIE plugins and Bio-YODIE initializer
#
FROM cogstacksystems/nlp-rest-service-gate:dev-latest AS bioyodie-initializer

# copy the GATE Bio-Yodie application
WORKDIR /gate/app/bioyodie

COPY --from=bioyodie-builder /build/Bio-YODIE /gate/app/bioyodie

# copy the bioyodie resources
WORKDIR /umls/

COPY ./bio-yodie/bio-yodie-resources ./bio-yodie-resources
RUN ln -s /umls/bio-yodie-resources /gate/app/bioyodie/bio-yodie-resources


# copy the helper scripts 
WORKDIR /app/nlp-service
COPY ./bio-yodie/config /app/nlp-service/config

# run the initialization of GATE Embedded to download necessary plugins
RUN ["bash", "init_gate.sh"]




################################
#
# GATE Service Runner with ANNIE plugins
#
FROM cogstacksystems/nlp-rest-service-gate:dev-latest

# copy the GATE Bio-Yodie application
WORKDIR /gate/app/bioyodie
COPY --from=bioyodie-builder /build/Bio-YODIE /gate/app/bioyodie

# copy the initialized plugins
WORKDIR /root
COPY --from=bioyodie-initializer /root/.m2 /root/.m2

# copy the helper scripts
WORKDIR /app/nlp-service
COPY ./bio-yodie/config /app/nlp-service/config

# just spawn the shell
CMD ["/bin/bash"]
