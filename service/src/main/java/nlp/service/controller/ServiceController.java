package nlp.service.controller;

import nlp.common.model.protocol.NlpProcessingResult;
import nlp.common.model.protocol.ProcessingError;
import nlp.common.model.protocol.ServiceRequestContent;
import nlp.common.model.protocol.ServiceResponseContent;
import nlp.service.config.ServiceConfiguration;
import nlp.service.service.NlpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Generic NLP service controller
 */
@RestController
public class ServiceController {

    @Autowired
    ServiceConfiguration config;

    /**
     * Endpoints path specific configuration
     * // TODO: specify in the Service configuration file
     */
    private final String apiPathPrefix = "/**/api";
    //private final String apiVersion = "v1";
    private final String apiFullPath = apiPathPrefix;


    /**
     * The NLP service used.
     */
    private NlpService service;

    private Logger log = LoggerFactory.getLogger(ServiceController.class);


    @PostConstruct
    public void init() throws Exception {
        service = getNlpService(config);
    }


    /**
     * Instantiates the NLP application service Bean according to provided configuration.
     */
    private NlpService getNlpService(ServiceConfiguration config) throws Exception {
        try {
            String appClassName = config.getAppClassName();
            return (NlpService) (Class.forName(appClassName)
                    .getConstructor(ServiceConfiguration.class)
                    .newInstance(config));
        }
        catch (Exception e) {
            log.error("Cannot instantiate the NLP service: " + e.getMessage());
            throw e;
        }
    }


    /**
     * Returns the information about running NLP service, incl. its configuration.
     */
    @GetMapping(value = apiFullPath + "/info")
    public ResponseEntity<ServiceConfiguration> info() {
        return new ResponseEntity<>(config, HttpStatus.OK);
    }


    /**
     * Processes the content.
     */
    @PostMapping(value = apiFullPath + "/process")
    public ResponseEntity<ServiceResponseContent> process(@RequestBody /*@Valid*/ ServiceRequestContent content) {

        ServiceResponseContent response = new ServiceResponseContent();

        try {
            NlpProcessingResult result = service.process(content.getNlpPayload(), content.getApplicationParams());
            response.setResult(result);
        }
        catch (Exception e) {
            String message = "Error processing the query: " + e.getMessage();
            log.error(message);

            NlpProcessingResult result = new NlpProcessingResult();
            result.setError(ProcessingError.builder().message(message).build());

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.setFooter(content.getFooter());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /*

    TODO: move to tests

    @GetMapping(value = apiPathPrefix + "/test")
    public ResponseEntity<ServiceResponseContent> test() {

        final String text = "Pt is 40yo mother, software engineer HPI : Sleeping trouble on present " +
                "dosage of Clonidine. Severe Rash  on face and leg, slightly itchy  Meds : Vyvanse " +
                "50 mgs po at breakfast daily, Clonidine 0.2 mgs -- 1 and 1 / 2 tabs po qhs HEENT : " +
                "Boggy inferior turbinates, No oropharyngeal lesion Lungs : clear Heart : Regular rhythm " +
                "Skin :  Papular mild erythematous eruption to hairline Follow-up as scheduled";

        //String text = "Theresa May suffered a second defeat in two days as Parliament asserted its authority " +
        //        "over Brexit. MPs voted by 308 votes to 297 – a majority of 11 – on an amendment to force the " +
        //        "Prime Minister to return to the House and make a statement about a “plan B”  " +
        //        "within three days if she loses.";

        // process the nlp
        //
        ServiceRequestContent content = new ServiceRequestContent();
        //content.setDocumentContent(text);

        ServiceResponseContent nlpResult;
        try {
            nlpResult = service.process(content, Collections.emptyMap());
        }
        catch (Exception e) {
            log.error("Error processing the query: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(nlpResult, HttpStatus.OK);
    }
    */
}
