package nlp.common.model.data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import nlp.common.model.protocol.ProcessingError;
import nlp.common.model.protocol.NlpProcessingResult;


/**
 * A generic abstract class used as a base to implement mapping of the NLP
 * application-specific processing result to a generic result payload (builder).
 */
public abstract class NlpResultDataMapper {

    protected NlpProcessingResult result;

    public NlpResultDataMapper() {
        result = new NlpProcessingResult();
        result.setMetadata(new HashMap<>());
        result.setSuccess(true);
        result.setTimestamp(OffsetDateTime.now());
    }

    public void setError(ProcessingError error) {
        if (result.getSuccess()) {
            result.setSuccess(false);
            result.setErrors(new ArrayList<>());
        }
        result.getErrors().add(error);
    }

    public NlpProcessingResult getProcessingResult() {
        return result;
    }
}
