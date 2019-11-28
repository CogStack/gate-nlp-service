package nlp.common.model.data;

import nlp.common.model.protocol.NlpInputPayload;

/**
 * A generic abstract class used as a base to implement NLP
 * application-specific access to the input payload.
 */
public abstract class NlpContentDataMapper {
    protected NlpInputPayload payload;

    public NlpContentDataMapper(NlpInputPayload payload) {
        this.payload = payload;
    }

}
