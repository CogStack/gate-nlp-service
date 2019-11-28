package nlp.common.model.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;


/**
 * The content of the Body part in HTTP request that will be consumed by the NLP REST service
 */
@Data
public class ServiceBulkRequestContent {
    /**
     * The content to be processed by the NLP service.
     */
    @JsonProperty("content")
    List<NlpInputPayload> content;

    /**
     * The list of possible application-specific parameters.
     */
    @JsonProperty("application_params")
    Map<String, String> applicationParams;
}
