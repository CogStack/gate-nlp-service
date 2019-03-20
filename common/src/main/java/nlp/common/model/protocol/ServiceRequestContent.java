package nlp.common.model.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;


/**
 * The content of the Body part in HTTP request that will be consumed by the NLP REST service
 */
@Data
public class ServiceRequestContent {
    /**
     * The content to be processed by the NLP service.
     */
    @JsonProperty("content")
    NlpInputPayload nlpPayload;

    /**
     * The list of possible application-specific parameters.
     */
    @JsonProperty("application_params")
    Map<String, String> applicationParams;

    /**
     *  Auxiliary data provided by client application that will be returned back
     */
    @JsonProperty("footer")
    Map<String, Object> footer;
}
