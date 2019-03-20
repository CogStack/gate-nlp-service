package nlp.common.model.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;


/**
 * The content of the Body part in HTTP request that will be output by the NLP REST service
 */
@Data
public class ServiceResponseContent {
    /**
     * The NLP processing result.
     */
    @JsonProperty("result")
    NlpProcessingResult result;

    /**
     * Auxiliary data provided by the client application
     */
    @JsonProperty("footer")
    Map<String, Object> footer;
}
