package nlp.gateway.model.brcapi;

import nlp.common.model.annotation.GenericAnnotation;

import lombok.Data;
import java.util.List;


@Data
public class NlpProcessorResultDescription extends ServiceDescription {
    Boolean success;
    List<Error> errors;
    List<GenericAnnotation> results;
}
