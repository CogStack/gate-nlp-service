package nlp.service.gate.processor;

import java.io.File;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogManager;
import gate.Corpus;
import gate.CorpusController;
import gate.Factory;
import gate.Gate;
import gate.Document;
import gate.util.persistence.PersistenceManager;
import nlp.common.model.annotation.GenericAnnotation;
import nlp.common.model.document.GenericDocument;
import nlp.service.gate.utils.GateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * GATE Document processor
 */
public class GateProcessor {

    /**
     * Use by default only one gate corpus controller to do NLP processing.
     */
    private static final int DEFAULT_GATE_CONTROLLERS_NUM = 1;

    /**
     * Available parameters that can be specified during runtime.
     */
    private class GateApplicationRuntimeParamKeys {
        static final String ANNOTATION_SETS = "annotationSets";
    }

    /**
     * GATE corpus controller pool for support for parallel processing of documents.
     */
    private BlockingQueue<CorpusController> controllerPool;

    /**
     * The available annotation sets with types as defined by the application designer,
     * can be specified as "*:*" which would be providing all the annotations.
     */
    private Map<String, Set<String>> availableAnnotationSets;


    private Logger log = LoggerFactory.getLogger(GateProcessor.class);


    public GateProcessor(GateApplicationSetupParameters params) throws Exception {

        initGateFramework(params);
        log.info("GATE framework initialized");

        initGateResources(params);
        log.info("GATE NLP application initialized");

        parseAdditionalAppParams(params);
        log.info("GATE NLP application configuration set");

        // decrease verbosity of some of the GATE plugins (if used)
        if (LogManager.getLogManager().getLogger("HeidelTimeWrapper") != null) {
            LogManager.getLogManager().getLogger("HeidelTimeWrapper").setLevel(Level.WARNING);
        }
    }


    /**
     * Processes provided single Generic Document and extract all the annotations (w. document-level features).
     */
    public GenericDocument processDocument(GenericDocument inDocument,
                                           Map<String, String> runtimeParams) throws Exception {

        // check whether the document is empty
        //
        if (GateUtils.isBlank(inDocument.getText())) {
            log.info("Provided document contains only whitespace characters");
            GenericDocument outDoc = new GenericDocument();
            outDoc.setText(inDocument.getText());
            return outDoc;
        }

        // process the document and extract the annotations
        //
        Document gateDoc;
        CorpusController gateController = controllerPool.take();

        try {
            log.info("Executing GATE controller: " + gateController.getName());

            // TODO:
            // set-up the document meta-data,
            // such as DCT using the runtime params
            gateDoc = Factory.newDocument(inDocument.getText());

            processGateDocument(inDocument, runtimeParams, gateController, gateDoc);
        }
        catch (Exception e) {
            log.error("Error executing GATE controller on the provided NLP query: " + e.getMessage());
            throw e;
        }
        finally {
            controllerPool.add(gateController);
        }

        // extract the annotations and prepare the output document
        //
        GenericDocument outDocument = prepareOutputDocument(gateDoc, runtimeParams);


        // cleanup
        //
        Factory.deleteResource(gateDoc);

        return outDocument;
    }


    /**
     * Processes provided documents in bulk and extract all the annotations (w. document-level features).
     */
    public List<GenericDocument> processDocumentsBulk(List<GenericDocument> inDocuments,
                                                      Map<String, String> runtimeParams) throws Exception {

        // we will be using only 1 GATE controller so we can assign the processed
        // annotations at specified indices
        //
        List<GenericDocument> outDocuments = new ArrayList<>(Collections.nCopies(inDocuments.size(), null));
        List<Document> gateDocuments = new ArrayList<>(Collections.nCopies(inDocuments.size(), null));

        // prepare GATE documents for processing
        //
        try {
            for (int i = 0; i < inDocuments.size(); ++i) {

                GenericDocument doc = inDocuments.get(i);

                // check whether the text is blank -- do not add such documents as GATE controller won't handle these
                //
                if (GateUtils.isBlank(doc.getText())) {
                    log.info("Provided document (idx: " + i + ") contains only whitespace characters");
                    gateDocuments.set(i, null);
                }
                else {
                    // TODO:
                    // set-up the document meta-data,
                    // such as DCT using the runtime params
                    gateDocuments.set(i, Factory.newDocument(doc.getText()));
                }
            }
        }
        catch (Exception e) {
            log.error("Error creating GATE documents for processing: " + e.getMessage());
            throw e;
        }
        finally {
            // cleanup the documents
            for (Document doc : gateDocuments) {
                if (doc != null) {
                    Factory.deleteResource(doc);
                }
            }
        }

        // run the GATE controller
        //
        CorpusController gateController = controllerPool.take();
        Corpus corpus = gateController.getCorpus();
        try
        {
            for (Document doc : gateDocuments) {
                if (doc != null) {
                    corpus.add(doc);
                }
            }

            log.info("Executing GATE controller: " + gateController.getName());
            gateController.execute();
        }
        catch (Exception e) {
            log.error("Error executing GATE controller on the provided bulk query: " + e.getMessage());
            throw e;
        }
        finally {
            corpus.clear();
            controllerPool.add(gateController);
        }

        // get the annotations
        //
        for (int i = 0; i < gateDocuments.size(); ++i) {
            Document doc = gateDocuments.get(i);

            if (doc == null) {
                outDocuments.set(i, new GenericDocument());
            } else {
                GenericDocument outDoc = prepareOutputDocument(doc, runtimeParams);
                outDocuments.set(i, outDoc);
            }
        }

        // cleanup
        //
        for (Document doc : gateDocuments) {
            if (doc != null) {
                Factory.deleteResource(doc);
            }
        }

        return outDocuments;
    }


    /**
     * Initializes GATE framework according to the specified configuration.
     */
    private void initGateFramework(GateApplicationSetupParameters params) throws Exception {
        try {
            if (!Gate.isInitialised()) {
                Gate.init();
            }
        }
        catch (Exception e) {
            log.error("Error initializing GATE framework: " + e.getMessage());
            throw e;
        }
    }


    /**
     * Initializes internal GATE resources according to the specified configuration.
     */
    private void initGateResources(GateApplicationSetupParameters params) throws Exception {
        try {
            controllerPool = new LinkedBlockingQueue<>();

            // use by default only one controller
            int numControllers = DEFAULT_GATE_CONTROLLERS_NUM;
            if (params.gateControllersNum > 0) {
                numControllers = params.gateControllersNum;
            }

            // create the initial controller
            CorpusController gateControllerTemplate = (CorpusController) PersistenceManager
                    .loadObjectFromFile(new File(params.getGateAppPath()));
            gateControllerTemplate.setName("GateCorpusController-0");

            Corpus corpus = Factory.newCorpus("defaultCorpus-" + numControllers);
            gateControllerTemplate.setCorpus(corpus);

            controllerPool.add(gateControllerTemplate);

            // we need to set up independent gate controllers to provide a thread-safe access to gate resources
            // in case of parallel processing
            for (int i = 1; i < numControllers; ++i) {
                CorpusController controllerDuplicate = (CorpusController)Factory.duplicate(gateControllerTemplate);
                controllerDuplicate.setName("GateCorpusController-" + Integer.toString(i));
                controllerPool.add(controllerDuplicate);
            }
        }
        catch (Exception e) {
            log.error("Error initializing GATE NLP application: " + e.getMessage());
            throw e;
        }
    }


    /**
     * Parses additional parameters provided as key-values in the configuration.
     */
    private void parseAdditionalAppParams(GateApplicationSetupParameters params) {
        if (params.getAnnotationSets() != null && params.getAnnotationSets().length() > 0) {
            availableAnnotationSets = GateUtils.getAnnotationTypeSets(params.getAnnotationSets());
        }
    }


    /**
     * Process a single document.
     */
    private void processGateDocument(GenericDocument inDocument,
                                     Map<String, String> runtimeParams,
                                     CorpusController gateController,
                                     Document outDocument) throws Exception {

        // TODO: use annotations from the input documents
        // TODO: use the runtime parameters during the processing
        runGateController(gateController, List.of(outDocument));
    }


    /**
     * Run a GateController over a list of GATE documents.
     */
    private void runGateController(CorpusController gateController,
                                   List<Document> documents) throws Exception {

        Corpus corpus = gateController.getCorpus();
        try {
            corpus.addAll(documents);
            gateController.execute();
        }
        finally {
            corpus.clear();
        }
    }


    /**
     * Extracts annotations from processed GATE document.
     */
    private List<GenericAnnotation> extractAnnotations(Document gateDoc, Map<String, String> applicationParams) {

        // perform filtering of annotations when provided by either:
        // - configuration file (init)
        // - client app (query)
        Map<String, Set<String>> annSets = null;
        if (availableAnnotationSets != null && availableAnnotationSets.size() > 0)
            annSets = new HashMap<>(availableAnnotationSets);

        if (applicationParams != null && applicationParams.containsKey(GateApplicationRuntimeParamKeys.ANNOTATION_SETS)) {
            Map<String, Set<String>> queryAnnSet = GateUtils.getAnnotationTypeSets(applicationParams.get(GateApplicationRuntimeParamKeys.ANNOTATION_SETS));
            if (annSets == null)
                annSets = queryAnnSet;
            else
                annSets = GateUtils.getAnnotationTypesSetsIntersection(annSets, queryAnnSet);
        }

        // select appropriate annotations set
        List<GenericAnnotation> anns;
        if (annSets != null)
            anns = GateUtils.getAtomicAnnotations(gateDoc, annSets);
        else
            anns = GateUtils.getAtomicAnnotations(gateDoc);

        // refine the annotations
        GateUtils.refineAtomicAnnotations(anns, gateDoc);

        return anns;
    }


    /**
     * Extracts document-level features from processed GATE document.
     */
    private GenericAnnotation extractFeatures(Document gateDoc, Map<String, String> applicationParams) {
        if (gateDoc.getFeatures() == null)
            return null;

        GenericAnnotation features = new GenericAnnotation();
        gateDoc.getFeatures().forEach((name, val) -> features.setAttribute(name.toString(), val));
        return features;
    }


    /**
     * Prepares the output Generic Document.
     */
    private GenericDocument prepareOutputDocument(Document gateDoc, Map<String, String> runtimeParams) {
        GenericDocument outDoc = new GenericDocument();

        // TODO:
        // parse the applicationParams to decide whether
        // to include text and/or additional properties

        List<GenericAnnotation> anns = extractAnnotations(gateDoc, runtimeParams);
        GenericAnnotation feats = extractFeatures(gateDoc, runtimeParams);

        outDoc.setText(GateUtils.getDocumentText(gateDoc));
        outDoc.setAnnotations(anns);
        if (feats != null && feats.getAttributes().size() > 0) {
            outDoc.setDocumentFeatures(feats);
        }

        return outDoc;
    }
}
