package nlp.gateway.model.brcapi;

import lombok.Data;
import java.util.List;


@Data
public class NlpProcessingResult {
    // a copy of the text-specific metadata provided in the request
    Object metadata;

    // source text
    String text;

    List<NlpProcessorResultDescription> processors;
}
