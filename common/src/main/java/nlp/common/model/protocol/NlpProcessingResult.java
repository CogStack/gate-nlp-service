package nlp.common.model.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nlp.common.model.annotation.GenericAnnotation;


/**
 * The result of NLP processing over the input document.
 */
@Data
public class NlpProcessingResult {
    /**
     * The document content that was used or modified during processing.
     */
    @JsonProperty("text")
    String text;

    /**
     * The resulting annotations.
     */
    @JsonProperty("annotations")
    List<GenericAnnotation> annotations;

    /**
     * Additional data provided by the NLP application. Since the output will be
     * application-specific, we cannot enforce here any specific data types.
     * The data stored can include document-level features (GATE), binary document, etc.
     */
    @JsonProperty("metadata")
    Map<String, Object> metadata;

    /**
     * NLP processing status.
     * 'true' on success, 'false' otherwise.
     */
    @JsonProperty("success")
    Boolean success;

    /**
     * Possible errors.
     * (status: false)
     */
    @JsonProperty("errors")
    List<ProcessingError> errors;

    /**
     * Helper function for setting errors
     */
    public void setError(ProcessingError error) {
        success = false;
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
    }
}
