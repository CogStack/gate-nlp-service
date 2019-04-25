package nlp.service.gate.processor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import nlp.common.model.document.GenericDocument;
import nlp.service.gate.utils.GateUtils;
import nlp.service.gate.annotation.model.AtomicGateAnnotation;
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


    public GateProcessor(GateApplicationParameters params) throws Exception {

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


    public GenericDocument process(GenericDocument inDocument, Map<String, String> runtimeParams) throws Exception {

        // process the document and extract the annotations
        //
        Document gateDoc;
        CorpusController gateController = controllerPool.take();
        try {
            log.info("Executing GATE controller: " + gateController.getName());

            gateDoc = Factory.newDocument(inDocument.getText());

            // TODO:
            // set-up the document meta-data,
            // such as DCT using the runtime params

            processDocument(inDocument, runtimeParams, gateController, gateDoc);
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
        List<AtomicGateAnnotation> anns = extractAnnotations(gateDoc, runtimeParams);
        GenericDocument outDocument = prepareOutputDocument(gateDoc, anns, runtimeParams);


        // cleanup
        //
        Factory.deleteResource(gateDoc);

        return outDocument;
    }

    private void initGateFramework(GateApplicationParameters params) throws Exception {
        try {
            String gateHome = params.getGateHome();
            Gate.setGateHome(new File(gateHome));
            Gate.init();
        }
        catch (Exception e) {
            log.error("Error initializing GATE framework: " + e.getMessage());
            throw e;
        }
    }


    private void initGateResources(GateApplicationParameters params) throws Exception {
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


    private void parseAdditionalAppParams(GateApplicationParameters params) {
        if (params.getAnnotationSets() != null && params.getAnnotationSets().length() > 0) {
            availableAnnotationSets = GateUtils.getAnnotationTypeSets(params.getAnnotationSets());
        }
    }


    private void processDocument(GenericDocument inDocument,
                                 Map<String, String> runtimeParams,
                                 CorpusController gateController,
                                 Document outDocument) throws Exception {

        Corpus corpus = gateController.getCorpus();
        try {
            corpus.add(outDocument);

            gateController.execute();
        }
        finally {
            corpus.clear();
        }
    }


    private List<AtomicGateAnnotation> extractAnnotations(Document gateDoc, Map<String, String> applicationParams) {

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
        List<AtomicGateAnnotation> anns;
        if (annSets != null)
            anns = GateUtils.getAtomicAnnotations(gateDoc, annSets);
        else
            anns = GateUtils.getAtomicAnnotations(gateDoc);

        // refine the annotations
        GateUtils.refineAtomicAnnotations(anns, gateDoc);

        return anns;
    }


    private GenericDocument prepareOutputDocument(Document gateDoc,
                                            List<AtomicGateAnnotation> anns,
                                            Map<String, String> applicationParams) {
        GenericDocument outDoc = new GenericDocument();

        outDoc.setText(GateUtils.getDocumentText(gateDoc));
        outDoc.setAnnotations(anns);

        // TODO:
        // parse the applicationParams to decide whether
        // to include text and/or additional properties

        return outDoc;
    }
}
