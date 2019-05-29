package nlp.service.gate.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import nlp.common.model.annotation.GenericAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gate.Document;
import gate.AnnotationSet;
import gate.Annotation;
import gate.FeatureMap;


/**
 * A helper class implementing useful utilities for working with GATE framework
 */
public class GateUtils {

    /**
     * Used to handle annotation sets filtering
     */
    private static final String GATE_DEFAULT_ANNOTATION_SET_NAME = "";
    private static final String FILTER_MATCH_ANY = "*";

    private static Logger log = LoggerFactory.getLogger(GateUtils.class);


    /**
     * Extracts ALL annotations from the GATE document.
     */
    public static List<GenericAnnotation> getAtomicAnnotations(Document gateDoc) {
        List<GenericAnnotation> atomicAnns = new ArrayList<>();

        // get the default annotation set -- all if filters are not specified
        AnnotationSet defaultSet = gateDoc.getAnnotations(GATE_DEFAULT_ANNOTATION_SET_NAME);
        defaultSet.forEach(ann -> atomicAnns.add(GateUtils.toAtomicAnnotation(ann, GATE_DEFAULT_ANNOTATION_SET_NAME)));

        // get all the annotation sets
        gateDoc.getAnnotationSetNames().forEach(setName ->
                gateDoc.getAnnotations(setName).forEach(ann ->
                        atomicAnns.add(GateUtils.toAtomicAnnotation(ann, setName))));

        return atomicAnns;
    }

    /**
     * Extracts annotations from the GATE document according to specified annotation sets.
     */
    public static List<GenericAnnotation> getAtomicAnnotations(Document gateDoc,
                                                               Map<String, Set<String>> annotationTypesSets) {
        List<GenericAnnotation> atomicAnns = new ArrayList<>();

        // get all types without the ann set names: *:type
        //
        if (annotationTypesSets.containsKey(FILTER_MATCH_ANY)) {
            // need to filter the '*' for compatibility with GATE
            Set<String> typeNames = new HashSet<>(annotationTypesSets.get(FILTER_MATCH_ANY));
            typeNames.remove(FILTER_MATCH_ANY);

            // go through the default set
            // TODO: or shall it be specially handled ???
            AnnotationSet defaultSet = gateDoc.getAnnotations(GATE_DEFAULT_ANNOTATION_SET_NAME);
            defaultSet.get(typeNames)
                    .forEach(annotation -> atomicAnns.add(GateUtils.toAtomicAnnotation(annotation, GATE_DEFAULT_ANNOTATION_SET_NAME)));

            // go though the named annotation sets
            gateDoc.getAnnotationSetNames()
                    .forEach(setName -> gateDoc.getAnnotations(setName).get(typeNames)
                            .forEach(annotation -> atomicAnns.add(GateUtils.toAtomicAnnotation(annotation, GATE_DEFAULT_ANNOTATION_SET_NAME)))
                    );

            // remove the MATCH_ANY filter and continue with the remaining filters
            annotationTypesSets.remove(FILTER_MATCH_ANY);
        }

        // get all named sets: set:type and set:*
        //
        Map<String, Set<String>> remainingSets = new HashMap<>(annotationTypesSets);
        remainingSets.remove(FILTER_MATCH_ANY);

        for (Map.Entry<String, Set<String>> pair : remainingSets.entrySet()) {
            // need to filter the '*' for compatibility with GATE
            Set<String> typeNames = new HashSet<>(pair.getValue());
            typeNames.remove(FILTER_MATCH_ANY);

            if (typeNames.size() == 0) {
                gateDoc.getAnnotations(pair.getKey())
                        .forEach(annotation -> atomicAnns.add(GateUtils.toAtomicAnnotation(annotation, pair.getKey())));
            } else {
                gateDoc.getAnnotations(pair.getKey())
                        .get(typeNames)
                        .forEach(annotation -> atomicAnns.add(GateUtils.toAtomicAnnotation(annotation, pair.getKey())));
            }
        }

        return atomicAnns;
    }

    /**
     * Refines the annotations to include the text they refer to.
     */
    public static void refineAtomicAnnotations(List<GenericAnnotation> atomicAnnotations, Document gateDoc) {
        for (GenericAnnotation ann : atomicAnnotations) {
            if (ann.getAttributes().containsKey("start_idx") && ann.getAttributes().containsKey("end_idx")) {
                Long startIdx = (Long)ann.getAttributes().get("start_idx");
                Long endIdx = (Long)ann.getAttributes().get("end_idx");
                String text = gate.Utils.stringFor(gateDoc, startIdx, endIdx);
                ann.setAttribute("text", text);
            }
        }
    }

    /**
     * Converts from GATE annotation type.
     */
    private static GenericAnnotation toAtomicAnnotation(Annotation gateAnnotation, String setName) {
        GenericAnnotation atomicAnn = new GenericAnnotation();

        // TODO:
        // implement it as a converter that will take care of deciding which fields to include
        // (some may be desirable to be skipped)

        // mandatory
        atomicAnn.setAttribute("type", gateAnnotation.getType());
        atomicAnn.setAttribute("start_idx", gateAnnotation.getStartNode().getOffset());
        atomicAnn.setAttribute("end_idx", gateAnnotation.getEndNode().getOffset());

        // attributes / features
        atomicAnn.setAttribute("set", setName);
        atomicAnn.setAttribute("id", gateAnnotation.getId());
        atomicAnn.setAttribute("start_node_id", gateAnnotation.getStartNode().getId().toString());
        atomicAnn.setAttribute("end_node_id", gateAnnotation.getEndNode().getId().toString());

        // gate features
        FeatureMap features = gateAnnotation.getFeatures();
        features.entrySet().forEach(entry ->
                atomicAnn.getAttributes().put(entry.getKey().toString(), entry.getValue()));

        return atomicAnn;
    }

    /**
     * Extracts the annotations/type sets from provided parameter string.
     */
    public static Map<String, Set<String>> getAnnotationTypeSets(String filterByAnnotations) {

        // firstly, get the list of filters, which are provided as a comma-separated list
        // in form: set1:type1, set2:type2, ...
        String[] filterPairs = filterByAnnotations.split(",");

        // set-name : type-names
        Map<String, Set<String>> annTypeSet = new HashMap<>();

        for (String filterPair : filterPairs) {

            assert  filterPair.contains(":");

            String[] tokens = filterPair.trim().split(":");
            assert tokens.length == 2;

            if (!filterPair.contains(":") || tokens.length != 2) {
                log.warn("Invalid filtering string specified: \"" + filterPair + "\". Skipping...");
                continue;
            }

            String setName = tokens[0].trim();
            String typeName = tokens[1].trim();

            // filter by specific group:* or group:type
            if (!setName.equals(FILTER_MATCH_ANY)) {

                if (!annTypeSet.containsKey(setName))
                    annTypeSet.put(setName, new HashSet<>());

                annTypeSet.get(setName).add(typeName);
            }  // filter by *:type
            else if (!typeName.equals(FILTER_MATCH_ANY)) {
                    if (!annTypeSet.containsKey(FILTER_MATCH_ANY))
                        annTypeSet.put(FILTER_MATCH_ANY, new HashSet<>());

                    annTypeSet.get(setName).add(typeName);
            } else {
                annTypeSet.put(FILTER_MATCH_ANY, new HashSet<>(Arrays.asList(FILTER_MATCH_ANY)));
            }
        }

        // TODO: perform post-filtering to handle cases such as:
        // - set1:type1, set1:*
        // - set1:type1, *:type1

        return annTypeSet;
    }

    /**
     * Perform intersection between specified filters.
     */
    public static Map<String, Set<String>> getAnnotationTypesSetsIntersection(Map<String, Set<String>> typesSets1,
                                                                              Map<String, Set<String>> typesSets2) {
        Map<String, Set<String>> resultSets = new HashMap<>();

        // TODO: special case: group is '*'

        for (Map.Entry<String, Set<String>> entry1 : typesSets1.entrySet()) {
            if (typesSets2.containsKey(entry1.getKey())) {
                String k = entry1.getKey();
                Set<String> set1 = entry1.getValue();
                Set<String> set2 = typesSets2.get(k);

                // special cases: any type is '*'
                if (set1.equals(set2)) {
                    resultSets.put(k, set1);
                }
                else if (set1.size() == 1 && set1.contains(FILTER_MATCH_ANY)) {
                    resultSets.put(k, set2);
                }
                else if (set2.size() == 1 && set2.contains(FILTER_MATCH_ANY)) {
                    resultSets.put(k, set1);
                }
                // standard case:
                else {
                    Set<String> rs = new HashSet<>(set1);
                    rs.retainAll(set2);
                    resultSets.put(k, rs);
                }
            }
        }

        return resultSets;
    }

    /**
     * Extracts the text from the GATE document.
     */
    public static String getDocumentText(Document gateDoc) {
        return gate.Utils.stringFor(gateDoc, 0L, gate.Utils.lengthLong(gateDoc));
    }

    /**
     * Checks whether the string is blank.
     */
    public static Boolean isBlank(String text) {
        return text.chars().allMatch(Character::isWhitespace);
    }
}
