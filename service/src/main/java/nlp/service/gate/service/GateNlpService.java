package nlp.service.gate.service;

import nlp.common.model.document.GenericDocument;
import nlp.common.model.protocol.NlpInputPayload;
import nlp.common.model.protocol.NlpProcessingResult;
import nlp.common.model.protocol.ProcessingError;
import nlp.service.config.ServiceConfiguration;
import nlp.service.gate.data.GateNlpContentDataMapper;
import nlp.service.gate.data.GateNlpResultDataMapper;
import nlp.service.gate.processor.GateApplicationParameters;
import nlp.service.gate.processor.GateProcessor;
import nlp.service.service.NlpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;


/**
 * Implements the NLP Service based on GATE framework
 */
public class GateNlpService extends NlpService {

    /**
     * Available configuration parameters for GATE
     */
    private class GateApplicationConfigurationKeys {
        static final String GATE_HOME = "gateHome";
        static final String GATE_APP_PATH = "gateAppPath";
        static final String GATE_CONTROLLER_NUM = "gateControllerNum";
        static final String ANNOTATION_SETS = "annotationSets";
    }

    /**
     * Used GATE documents processor
     */
    private GateProcessor gateProcessor;


    private Logger log = LoggerFactory.getLogger(GateNlpService.class);


    public GateNlpService(ServiceConfiguration config) throws Exception {
        super(config);

        GateApplicationParameters gateParams = parseAppParams(config);

        gateProcessor = new GateProcessor(gateParams);
    }


    @Override
    public NlpProcessingResult process(NlpInputPayload payload, Map<String, String> applicationParams) throws Exception {

        // parse the payload to GenericDocument handler
        //
        GateNlpContentDataMapper contentMapper = new GateNlpContentDataMapper(payload);
        GateNlpResultDataMapper resultMapper = new GateNlpResultDataMapper();

        GenericDocument doc = new GenericDocument();
        doc.setText(contentMapper.getText());

        if (contentMapper.getAnnotations().size() > 0) {
            doc.setAnnotations(contentMapper.getAnnotations());
        }

        // TODO: handle (if required):
        // - document-level features
        // - linked attributes
        // - binary document

        // run GATE processor
        //
        GenericDocument outDoc;
        try {
            outDoc = gateProcessor.process(doc, applicationParams);
        } catch (Exception e) {
            String message = "Error processing NLP query: " + e.getMessage();
            log.error(message);
            throw e;
        }

        // prepare the result payload
        //
        resultMapper.setText(outDoc.getText());
        resultMapper.setAnnotations(outDoc.getAnnotations());
        resultMapper.setDocumentFeatures(outDoc.getDocumentFeatures());

        // TODO: set in payload (if required):
        // - binary document
        // - enable/disable text inclusion

        return resultMapper.getProcessingResult();
    }


    private GateApplicationParameters parseAppParams(ServiceConfiguration config) throws Exception {

        if (!config.getAppParams().containsKey(GateApplicationConfigurationKeys.GATE_HOME)) {
            throw new Exception("GATE_HOME not set");
        }

        if (!config.getAppParams().containsKey(GateApplicationConfigurationKeys.GATE_APP_PATH)) {
            throw new Exception("GATE_APP_PATH not set");
        }

        GateApplicationParameters gateParams = new GateApplicationParameters();

        gateParams.setGateHome((String)config.getAppParams().get(GateApplicationConfigurationKeys.GATE_HOME));
        gateParams.setGateAppPath((String)config.getAppParams().get(GateApplicationConfigurationKeys.GATE_APP_PATH));

        if (config.getAppParams().containsKey(GateApplicationConfigurationKeys.GATE_CONTROLLER_NUM)) {
            gateParams.setGateControllersNum(Integer.parseInt(config.getAppParams()
                    .get(GateApplicationConfigurationKeys.GATE_CONTROLLER_NUM).toString()));
        }

        if (config.getAppParams().containsKey(GateApplicationConfigurationKeys.ANNOTATION_SETS)) {
            gateParams.setAnnotationSets((String)config.getAppParams().get(GateApplicationConfigurationKeys.ANNOTATION_SETS));
        }

        return gateParams;
    }

}
