package nlp.common.model.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;


/**
 * The content of the Body part in HTTP request that will be output by the NLP REST service
 */
@Data
public class ServiceBulkResponseContent {
    /**
     * The NLP processing result.
     */
    @JsonProperty("result")
    List<NlpProcessingResult> result;
}
