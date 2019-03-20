package nlp.common.model.annotation;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The generic annotation type that is used by NLP applications
 */
@Data
public class GenericAnnotation {

    /**
     * The annotation consist only of attributes represented as KVPs.
     * It is the responsibility of upstream NLP application to parse them.
     */
    @JsonIgnore
    Map<String, Object> attributes = new HashMap<>();


    @JsonAnyGetter
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @JsonAnySetter
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
}
