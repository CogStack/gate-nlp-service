package nlp.gate.service;

import nlp.common.model.document.GenericDocument;
import nlp.common.model.protocol.NlpInputPayload;
import nlp.common.model.protocol.NlpProcessingResult;
import nlp.service.config.ApplicationConfiguration;
import nlp.gate.data.GateNlpContentDataMapper;
import nlp.gate.data.GateNlpResultDataMapper;
import nlp.gate.processor.GateApplicationSetupParameters;
import nlp.gate.processor.GateProcessor;
import nlp.service.NlpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Implements the NLP Service based on GATE framework
 */
public class GateNlpService extends NlpService {

    /**
     * Available configuration parameters for GATE
     */
    private class GateApplicationConfigurationKeys {
        // Starting from GATE 8.5 GATE_HOME is not needed anymore
        //static final String GATE_HOME = "gateHome";
        static final String GATE_APP_PATH = "gateAppPath";
        static final String GATE_CONTROLLER_NUM = "gateControllerNum";
        static final String ANNOTATION_SETS = "gateAnnotationSets";
        static final String INCLUDE_ANNOTATION_TEXT = "gateIncludeAnnotationText";
    }

    /**
     * Used GATE documents processor
     */
    private GateProcessor gateProcessor;


    private Logger log = LoggerFactory.getLogger(GateNlpService.class);


    public GateNlpService(ApplicationConfiguration config) throws Exception {
        super(config);

        GateApplicationSetupParameters gateParams = parseAppParams(config);

        gateProcessor = new GateProcessor(gateParams);
    }


    /**
     * Process a single request.
     */
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
            outDoc = gateProcessor.processDocument(doc, applicationParams);
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


    /**
     * Process a bulk request.
     */
    @Override
    public List<NlpProcessingResult> processBulk(List<NlpInputPayload> payloads,
                                                 Map<String, String> applicationParams) throws Exception {

        // parse the payload to GenericDocument handler
        //
        List<GenericDocument> inputDocuments = new ArrayList<>();
        for (NlpInputPayload singlePayload : payloads) {
            GateNlpContentDataMapper contentMapper = new GateNlpContentDataMapper(singlePayload);

            GenericDocument doc = new GenericDocument();
            doc.setText(contentMapper.getText());

            if (contentMapper.getAnnotations().size() > 0) {
                doc.setAnnotations(contentMapper.getAnnotations());
            }

            // TODO: handle (if required):
            // - document-level features
            // - linked attributes
            // - binary document

            inputDocuments.add(doc);
        }

        // run GATE processor
        //
        List<GenericDocument> outDocs;
        try {
            outDocs = gateProcessor.processDocumentsBulk(inputDocuments, applicationParams);
        } catch (Exception e) {
            String message = "Error processing NLP query: " + e.getMessage();
            log.error(message);
            throw e;
        }

        // prepare the result payload
        //
        List<NlpProcessingResult> results = new ArrayList<>();
        for (GenericDocument doc : outDocs) {
            GateNlpResultDataMapper resultMapper = new GateNlpResultDataMapper();
            resultMapper.setText(doc.getText());
            resultMapper.setAnnotations(doc.getAnnotations());
            resultMapper.setDocumentFeatures(doc.getDocumentFeatures());

            // TODO: set in payload (if required):
            // - binary document
            // - enable/disable text inclusion

            results.add(resultMapper.getProcessingResult());
        }

        return results;
    }


    private GateApplicationSetupParameters parseAppParams(ApplicationConfiguration config) {

        GateApplicationSetupParameters gateParams = new GateApplicationSetupParameters();
        gateParams.setGateAppPath((String)config.getAppParams().get(GateApplicationConfigurationKeys.GATE_APP_PATH));

        if (config.getAppParams().containsKey(GateApplicationConfigurationKeys.GATE_CONTROLLER_NUM)) {
            gateParams.setGateControllersNum(Integer.parseInt(config.getAppParams()
                    .get(GateApplicationConfigurationKeys.GATE_CONTROLLER_NUM).toString()));
        }

        if (config.getAppParams().containsKey(GateApplicationConfigurationKeys.ANNOTATION_SETS)) {
            gateParams.setAnnotationSets((String)config.getAppParams().get(GateApplicationConfigurationKeys.ANNOTATION_SETS));
        }

        if (config.getAppParams().containsKey(GateApplicationConfigurationKeys.INCLUDE_ANNOTATION_TEXT)) {
            gateParams.setIncludeAnotationText((boolean)config.getAppParams().get(GateApplicationConfigurationKeys.INCLUDE_ANNOTATION_TEXT));
        }

        return gateParams;
    }

}
