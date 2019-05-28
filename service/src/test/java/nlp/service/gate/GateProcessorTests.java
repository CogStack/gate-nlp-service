package nlp.service.gate;

// JUnit 5
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import org.junit.jupiter.api.Test;

// using JUnit 4
//  for compatibility with SpringRunner which is based on Junit 4
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

import java.io.InputStream;
import java.util.*;

import nlp.service.utils.TestUtils;
import nlp.common.model.annotation.GenericAnnotation;
import nlp.common.model.document.GenericDocument;
import nlp.service.gate.processor.GateApplicationSetupParameters;
import nlp.service.gate.processor.GateProcessor;


/**
 * This class implements tests for testing the functionality of GATE Processor
 *  for extracting annotations from example documents.
 *  The example GATE application includes a simple Drug application identifying common
 *  drug names.
 */
public class GateProcessorTests {

    /**
     Test configuration file to be loaded
     */
    private static Properties testConfig;


    public GateProcessorTests() throws Exception {
        InputStream is = ClassLoader.getSystemResourceAsStream("gate-test.properties");
        testConfig = new Properties();
        testConfig.load(is);
    }


    @Test
    public void processEmptyDocument() throws Exception {
        GateApplicationSetupParameters params = createDefaultApplicationParameters();
        GateProcessor gateProcessor = new GateProcessor(params);

        GenericDocument inDoc = TestUtils.createEmptyDocument();
        GenericDocument outDoc = gateProcessor.process(inDoc, Collections.emptyMap());
        assertEquals(0, outDoc.getAnnotations().size());
    }


    @Test
    public void processBlankDocuments() throws Exception {
        GateApplicationSetupParameters params = createDefaultApplicationParameters();
        GateProcessor gateProcessor = new GateProcessor(params);

        List<GenericDocument> inDocs = TestUtils.createBlankDocuments();

        for (GenericDocument doc : inDocs) {
            GenericDocument outDoc = gateProcessor.process(doc, Collections.emptyMap());
            assertEquals(0, outDoc.getAnnotations().size());
        }
    }


    @Test
    public void processExampleShortDocument() throws Exception {
        GateApplicationSetupParameters params = createDefaultApplicationParameters();
        GateProcessor gateProcessor = new GateProcessor(params);

        GenericDocument inDoc = TestUtils.createShortDocument();
        GenericDocument outDoc = gateProcessor.process(inDoc, Collections.emptyMap());

        // list of annotations should not be empty
        List<GenericAnnotation> annotations = outDoc.getAnnotations();
        assertNotEquals(0, annotations.size());
    }


    @Test
    public void processExampleShortDocumentDrugNames() throws Exception {
        GateApplicationSetupParameters params = createApplicationParametersDrugNames();
        GateProcessor gateProcessor = new GateProcessor(params);

        GenericDocument inDoc = TestUtils.createShortDocument();
        GenericDocument outDoc = gateProcessor.process(inDoc, Collections.emptyMap());

        // there should be only two drug name annotations
        List<GenericAnnotation> annotations = outDoc.getAnnotations();
        assertEquals(1, annotations.size());

        // this annotation should be Prozac and listed as drug:medication
        GenericAnnotation ann = annotations.get(0);
        assertEquals("prozac", ann.getAttributes().get("name").toString().toLowerCase());
        assertEquals("drug", ann.getAttributes().get("majorType").toString().toLowerCase());
        assertEquals("medication", ann.getAttributes().get("minorType").toString().toLowerCase());
    }


    @Test
    public void processExampleACMDocumentDrugNames() throws Exception {
        GateApplicationSetupParameters params = createApplicationParametersDrugNames();
        GateProcessor gateProcessor = new GateProcessor(params);

        GenericDocument inDoc = TestUtils.createACMDocument();
        GenericDocument outDoc = gateProcessor.process(inDoc, Collections.emptyMap());

        // list of annotations should not be empty
        List<GenericAnnotation> annotations = outDoc.getAnnotations();
        assertNotEquals(0, annotations.size());
    }

    /**
     * Helper functions
     */
    private GateApplicationSetupParameters createDefaultApplicationParameters() throws Exception {
        GateApplicationSetupParameters params = new GateApplicationSetupParameters();
        params.setGateAppPath(testConfig.getProperty("gateAppPath"));
        params.setGateControllersNum(Integer.parseInt(testConfig.getProperty("gateControllerNum")));

        return params;
    }

    private GateApplicationSetupParameters createApplicationParametersDrugNames() throws  Exception {
        GateApplicationSetupParameters params = createDefaultApplicationParameters();

        params.setAnnotationSets("*:Drug");

        return params;
    }

}

