package nlp.gateway.model.brcapi;

import lombok.Data;


@Data
public class NlpRequestContent {
    String text;

    // TODO: shall we clarify the type here?
    Object metadata;
}
