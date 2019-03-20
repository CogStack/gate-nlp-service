package nlp.gateway.model.brcapi;

import lombok.Data;

import java.util.List;


@Data
public class ProcessCommand extends RequestCommand {

    List<NlpProcessorDescription> processors;

    Boolean useQueue;

    String clientJobId;

    Boolean includeText;

    NlpRequestContent content;
}
