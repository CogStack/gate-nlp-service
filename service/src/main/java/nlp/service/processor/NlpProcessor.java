package nlp.service.processor;

import java.util.List;
import java.util.Map;
import nlp.common.model.document.GenericDocument;


/**
 * Generic NLP Document processor absstract class
 */
public abstract class NlpProcessor {

    /**
     * Processes provided single Generic Document and extract all the annotations (w. document-level features).
     */
    public abstract GenericDocument processDocument(GenericDocument inDocument,
                                                    Map<String, String> runtimeParams) throws Exception ;

    /**
     * Processes provided documents in bulk and extract all the annotations (w. document-level features).
     */
    public abstract List<GenericDocument> processDocumentsBulk(List<GenericDocument> inDocuments,
                                                               Map<String, String> runtimeParams) throws Exception;
}
