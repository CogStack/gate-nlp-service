package nlp.service.utils;

import nlp.common.model.document.GenericDocument;
import java.util.ArrayList;


public class TestUtils {

    public static String getExampleShortText() {
        return "The patient was prescribed with Prozac 1 kg daily";
    }

    public static String getExampleLongText() {
        return "Pt is 40yo mother, software engineer " +
                "HPI : Sleeping trouble on present dosage of Clonidine. " +
                "Severe Rash  on face and leg, slightly itchy  " +
                "Meds : Vyvanse 50 mgs po at breakfast daily, " +
                "Clonidine 0.2 mgs -- 1 and 1 / 2 tabs po qhs " +
                "HEENT : Boggy inferior turbinates, No oropharyngeal lesion " +
                "Lungs : clear Heart : Regular rhythm " +
                "Skin :  Papular mild erythematous eruption to hairline Follow-up as scheduled.";
    }

    public static GenericDocument createEmptyDocument() {
        String content = "";
        return createDocument(content);
    }

    public static ArrayList<GenericDocument> createBlankDocuments() {
        ArrayList<GenericDocument> docs = new ArrayList<>();
        docs.add(TestUtils.createDocument(" "));
        docs.add(TestUtils.createDocument(" \n\n\n "));
        docs.add(TestUtils.createDocument(" \n   \n   \t "));
        docs.add(TestUtils.createDocument("\t"));
        docs.add(TestUtils.createDocument("\n\n\n \t \t    \t \n"));
        return docs;
    }

    public static GenericDocument createShortDocument() {
        return createDocument(getExampleShortText());
    }

    public static GenericDocument createACMDocument() {

        return createDocument(getExampleLongText());
    }

    public static GenericDocument createDocument(String text) {
        GenericDocument doc = new GenericDocument();
        doc.setText(text);
        return  doc;
    }
}
