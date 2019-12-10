package nlp.common.model.protocol;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nlp.common.model.annotation.GenericAnnotation;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * The result of NLP processing over the input document.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
     * NLP processing timestamp.
     */
    @JsonProperty("timestamp")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime timestamp;

    /**
     * Possible errors.
     * (status: false)
     */
    @JsonProperty("errors")
    List<ProcessingError> errors;

    /**
     * Auxiliary data provided by the client application that will be returned back.
     */
    @JsonProperty("footer")
    Map<String, Object> footer;

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
