package nlp.gate.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import nlp.common.model.annotation.GenericAnnotation;
import nlp.common.model.data.NlpContentDataMapper;
import nlp.common.model.protocol.NlpInputPayload;


/**
 * Implements handling of the content stored in generic payload to be accessible
 * by GATE-based applications.
 */
public class GateNlpContentDataMapper extends NlpContentDataMapper {

    public GateNlpContentDataMapper(NlpInputPayload payload) {
        super(payload);
    }

    public String getText() {
        if (payload.getText() != null)
            return payload.getText();
        return "";
    }

    public List<GenericAnnotation> getAnnotations() {
        if (payload.getMetadata() == null || !payload.getMetadata().containsKey("annotations"))
            return Collections.emptyList();

        Object obj = payload.getMetadata().get("annotations");
        if (obj instanceof List) {
            return (List<GenericAnnotation>)obj;
        }

        return Collections.emptyList();
    }

    public List<GenericAnnotation> getDocumentFeatures() {
        if (payload.getMetadata() == null || !payload.getMetadata().containsKey("document_features"))
            return Collections.emptyList();

        Object obj = payload.getMetadata().get("document_features");
        if (obj instanceof List) {
            List<GenericAnnotation> anns = (List<GenericAnnotation>)obj;
            return anns;
        }

        return Collections.emptyList();
    }

    public Byte[] getBinaryDocument() {
        if (payload.getMetadata() == null || !payload.getMetadata().containsKey("binary_document"))
            return ArrayUtils.toArray();

        Object obj = payload.getMetadata().get("binary_document");
        if (obj instanceof List) {
            Byte[] binaryDoc = (Byte[])payload.getMetadata().get("binary_document");
            return binaryDoc;
        }

        return ArrayUtils.toArray();
    }

    public Map<String, Object> getLinkedData() {
        Map<String, Object> attrs = new HashMap<>();
        if (payload.getMetadata() != null) {
            payload.getMetadata().forEach((field, value) -> {
                if (!field.equals("annotations") && !field.equals("document_features") && !field.equals("binary_document")) {
                    attrs.put(field, value);
                }
            });
        }
        return attrs;
    }
}
