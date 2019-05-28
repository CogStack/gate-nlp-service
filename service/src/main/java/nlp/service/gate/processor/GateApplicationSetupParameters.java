package nlp.service.gate.processor;

import lombok.Data;


/**
 * GATE NLP application-specific parameters
 */
@Data
public class GateApplicationSetupParameters {
    // Starting from GATE 8.5 GATE_HOME is no longer needed
    //String gateHome;

    String gateAppPath;
    int gateControllersNum;
    String annotationSets;
}
