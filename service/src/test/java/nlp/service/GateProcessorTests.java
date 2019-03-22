package nlp.service;

import nlp.common.model.document.GenericDocument;
import nlp.service.gate.processor.GateApplicationParameters;
import nlp.service.gate.processor.GateProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = GateProcessor.class)
public class GateProcessorTests {

    // TODO: load configuration
    private GateApplicationParameters gateParams = new GateApplicationParameters();

    private static GateProcessor gateProcessor;

    public GateProcessorTests() {
        gateParams.setGateControllersNum(0);
        gateParams.setGateHome("");
        gateParams.setGateAppPath("");
    }

    @Before
    public void initGateProcessor() throws Exception {
        if (gateProcessor == null) {
            gateProcessor = new GateProcessor(gateParams);
        }
    }

    @Test
    public void processDocument() throws Exception {
        //gateProcessor.process()
    }


    /**
     * Helper methods
     * TODO: can be moved to a separate class
     */
    private GenericDocument createEmptyDocument() {
        String content = "";
        return createDocument(content);
    }

    private GenericDocument createShortDocument() {
        String content = "";
        return createDocument(content);
    }

    private GenericDocument createLongDocument() {
        String content = "";
        return createDocument(content);
    }

    private GenericDocument createDocument(String text) {
        GenericDocument doc = new GenericDocument();
        doc.setText(text);
        return  doc;
    }

}

