package nlp.gateway.model.brcapi;

import lombok.Data;
import java.util.List;


@Data
public class ImmediateProcessingResponse extends ServiceResponse {
    String clientJobId;

    List<NlpProcessingResult> results;
}
