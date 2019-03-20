package nlp.service.model.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotNull;
import nlp.common.model.annotation.TextAnnotation;


/**
 * GATE-specific annotation type.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AtomicGateAnnotation extends TextAnnotation {

    @NotNull
    @JsonProperty("id")
    String id;

    @NotNull
    @JsonProperty("type")
    String type;

    @NotNull
    @JsonProperty("set")
    String set;

    @NotNull
    @JsonProperty("start_node_id")
    String startNodeId;

    @NotNull
    @JsonProperty("end_node_id")
    String endNodeId;
}
