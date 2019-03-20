package nlp.gateway.model.brcapi;

import nlp.gateway.model.protocol.ServiceArguments;

import lombok.Data;


// TODO: make compatible with service

@Data
public class ServiceRequest {

    ProtocolDescription protocol;

    // TODO: make as ENUM
    String command;

    // TODO: each applicaiton should refine this
    ServiceArguments args;
}
