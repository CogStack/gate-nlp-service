package nlp.common.model.annotation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;


/**
 * Specialized type used for handling the text-based annotations.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TextAnnotation extends GenericAnnotation {
    @NotNull
    @JsonProperty("start_idx")
    Long startIdx;

    @NotNull
    @JsonProperty("end_idx")
    Long endIdx;

    @JsonProperty("text")
    String text;
}
