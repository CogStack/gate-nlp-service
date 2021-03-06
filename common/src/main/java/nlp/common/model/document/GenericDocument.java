package nlp.common.model.document;

import nlp.common.model.annotation.GenericAnnotation;
import lombok.Data;

import java.util.*;


/**
 * Generic document model that is used when working with annotations
 */
@Data
public class GenericDocument {
    /**
     * The current document text.
     */
    String text = "";

    /**
     * The document annotations.
     * The annotations be either created by the NLP processor or
     * can be set as coming from other NLP processing steps.
     */
    List<GenericAnnotation> annotations = new ArrayList<>();

    /**
     * Document-level features, as e.g. used in GATE.
     */
    GenericAnnotation documentFeatures = new GenericAnnotation();

    /**
     * The document content in NLP application-specific binary format.
     * Can be used as a NLP content container when performing multi-step processing.
     */
    Byte[] binaryDocument;

    /**
     * Additional linked attributes, such as record fields, etc.
     */
    Map<String, Object> linkedAttributes = new HashMap<>();


    public void setAnnotations(List<? extends GenericAnnotation> anns) {
        if (anns != null) {
            annotations = new ArrayList<>();
            annotations.addAll(anns);
        } else {
            annotations = null;
        }
    }
}
