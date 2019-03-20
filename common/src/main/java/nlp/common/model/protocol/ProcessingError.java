package nlp.common.model.protocol;

import lombok.Builder;
import lombok.Data;


/**
 * Description of error that occurred during NLP processing.
 */
@Data
@Builder
public class ProcessingError {
    String message;
    String description;
}
