package nlp.service;

import nlp.common.model.protocol.NlpInputPayload;
import nlp.common.model.protocol.NlpProcessingResult;
import nlp.service.config.ApplicationConfiguration;

import java.util.List;
import java.util.Map;


/**
 * Abstract class for implementing NLP Services
 */
public abstract class NlpService {

    /**
     * NLP service configuration.
     */
    private ApplicationConfiguration config;


    public NlpService(ApplicationConfiguration config) {
        this.config = config;
     }

    public ApplicationConfiguration getConfig() {
        return config;
    }


    /**
     * This methods is used to perform NLP processing over the input payload.
     * Each specialized NLP-app-specific class should implement it.
     */
    public abstract NlpProcessingResult process(NlpInputPayload payload,
                                                Map<String, String> applicationParams) throws Exception;

    /**
     * This methods is used to perform NLP processing over the input payload.
     * Each specialized NLP-app-specific class should implement it.
     */
    public abstract List<NlpProcessingResult> processBulk(List<NlpInputPayload> payloads,
                                                          Map<String, String> applicationParams) throws Exception;
}
