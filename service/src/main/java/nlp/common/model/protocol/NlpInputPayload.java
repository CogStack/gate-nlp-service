package nlp.common.model.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;


/**
 * The content to be processed by NLP application.
 */
@Data
public class NlpInputPayload {
    /**
     * The raw text document content to be processed.
     */
    @JsonProperty("text")
    String text;

    /**
     * Additional data linked with the document to be processed that will be used by
     * the NLP application. These can include annotations, binary document, records fields, etc.
     * Results from the previous processing steps can be stored here.
     */
    @JsonProperty("metadata")
    Map<String, Object> metadata;

    /**
     *  Auxiliary data provided by client application that will be returned back.
     */
    @JsonProperty("footer")
    Map<String, Object> footer;

    /**
     * Checks whether the payload content is empty
     */
    public Boolean isEmpty() {
        return (text == null || text.length() == 0) && (metadata == null || metadata.size() == 0);
    }
}
