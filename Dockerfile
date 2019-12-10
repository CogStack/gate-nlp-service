################################################################
#
# BUILD STEPS
#

################################
#
# JDK base
#
FROM openjdk:11-jdk-slim AS jdk-11-base

RUN apt-get update && \
	apt-get install -y curl && \
	apt-get clean autoclean && \
	apt-get autoremove -y && \
	rm -rf /var/lib/apt/lists/*



################################
#
# Service Builder
#
FROM jdk-11-base AS service-builder

# setup the build environment
RUN mkdir -p /devel
WORKDIR /devel

COPY ./gradle/wrapper /devel/gradle/wrapper
COPY ./gradlew /devel/

RUN ./gradlew --version

COPY ./settings.gradle /devel/
COPY . /devel/


# build service
RUN ./gradlew :app:bootJar --no-daemon



################################################################
#
# RUN STEPS
#

################################
#
# JRE base
#
FROM openjdk:11-jre-slim AS jre-11-base

RUN apt-get update && \
	apt-get install -y curl unzip && \
	apt-get clean autoclean && \
	apt-get autoremove -y && \
	rm -rf /var/lib/apt/lists/*


################################
#
# Service Runner
#
FROM jre-11-base AS service-runner

# setup env
RUN mkdir -p /app/nlp-service
WORKDIR /app/nlp-service

# copy artifacts
COPY --from=service-builder /devel/app/build/libs/app-*.jar ./

# entry point
CMD /bin/bash



################################
#
# GATE Service Runner base
#
FROM service-runner AS service-runner-gate

# GATE directories structure:
# - core components: /gate/home/
# - custom user apps: /gate/app/
# - GATE GCP: /gate/gcp/

WORKDIR /gate/

# download and set up the main GATE bundle
RUN curl -L 'https://github.com/GateNLP/gate-core/releases/download/v8.5/gate-developer-8.5-distro.zip' > gate-developer-8.5-distro.zip && \
	unzip gate-developer-8.5-distro.zip && \
	mv gate-developer-8.5 home && \
	rm gate-developer-8.5-distro.zip

ENV GATE_HOME=/gate/home

# download the GCP
# -- warning: GCP v.3.0.1 uses GATE 8.5.1 and has a problem with a missing plugin
# -- GCP v.3.0 uses GATE 8.5
RUN curl -L 'https://github.com/GateNLP/gcp/releases/download/v3.0/gcp-dist-3.0-distro.zip' > gcp-dist-3.0-distro.zip && \
	unzip gcp-dist-3.0-distro.zip && \
	mv gcp-dist-3.0 gcp && \
	rm gcp-dist-3.0-distro.zip


# copy the helper scripts
WORKDIR /app/nlp-service
COPY ./scripts/*.sh ./

# run the service app
CMD ["/bin/bash", "run.sh"]