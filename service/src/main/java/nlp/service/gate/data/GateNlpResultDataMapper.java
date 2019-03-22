package nlp.service.gate.data;

import java.util.List;
import nlp.common.model.annotation.GenericAnnotation;
import nlp.common.model.data.NlpResultDataMapper;


/**
 * Implements the mapping of the GATE NLP processing result to the generic payload.
 */
public class GateNlpResultDataMapper extends NlpResultDataMapper {

    public GateNlpResultDataMapper() {
        super();
    }

    public void setText(String text) {
        result.setText(text);
    }

    public void setAnnotations(List<GenericAnnotation> annotations) {
        result.setAnnotations(annotations);
    }

    public void setDocumentFeatures(List<GenericAnnotation> features) {
        result.getMetadata().put("document_features", features);
    }

    public void setBinaryDocument(Byte[] binaryDoc) {
        result.getMetadata().put("binary_document", binaryDoc);
    }
}
