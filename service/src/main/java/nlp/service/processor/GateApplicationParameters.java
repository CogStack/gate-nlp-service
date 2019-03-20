package nlp.service.processor;

import lombok.Data;


/**
 * GATE NLP application-specific parameters
 */
@Data
public class GateApplicationParameters {
    String gateHome;
    String gateAppPath;
    int gateControllersNum;
    String annotationSets;
}
