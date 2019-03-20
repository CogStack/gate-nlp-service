package nlp.gateway.model.brcapi;

import nlp.gateway.model.protocol.ServiceArguments;

import lombok.Data;


@Data
public class NlpProcessorDescription extends ServiceDescription {

    Boolean isDefaultVersion;

    // TODO: reach application should refine this parameter
    // Map<String, String> args;
    ServiceArguments args;
}
