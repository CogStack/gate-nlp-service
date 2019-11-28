package nlp.service.controller;

import com.fasterxml.jackson.annotation.JsonView;
import nlp.common.model.protocol.*;
import nlp.service.config.ApplicationConfiguration;
import nlp.service.config.JsonPropertyAccessView;
import nlp.service.NlpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


/**
 * Generic NLP service controller
 */
@RestController
public class ServiceController {

    @Autowired
    ApplicationConfiguration config;

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
    private NlpService getNlpService(ApplicationConfiguration config) throws Exception {
        try {
            String appClassName = config.getAppClassName();
            return (NlpService) (Class.forName(appClassName)
                    .getConstructor(ApplicationConfiguration.class)
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
    @JsonView(JsonPropertyAccessView.Public.class)
    @GetMapping(value = apiFullPath + "/info")
    public ResponseEntity<ApplicationConfiguration> info() {
        return new ResponseEntity<>(config, HttpStatus.OK);
    }


    /**
     * Processes the content - a single document at once.
     */
    @PostMapping(value = apiFullPath + "/process")
    public ResponseEntity<ServiceSingleResponseContent> process(@RequestBody /*@Valid*/ ServiceSingleRequestContent content) {

        ServiceSingleResponseContent response = new ServiceSingleResponseContent();

        // check whether we need to perform any processing
        //
        if (content.getContent() == null || content.getContent().isEmpty()) {
            final String message = "Empty content";
            NlpProcessingResult result = new NlpProcessingResult();
            result.setError(ProcessingError.builder().message(message).build());
            response.setResult(result);
            log.info(message);
            HttpStatus status = config.isAppSingleDocEndpointFailOnEmptyContent() ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
            return new ResponseEntity<>(response, status);
        }

        // process the content
        //
        try {
            NlpProcessingResult result = service.process(content.getContent(), content.getApplicationParams());
            response.setResult(result);
        }
        catch (Exception e) {
            final String message = "Error processing the query: " + e.getMessage();
            log.error(message);

            NlpProcessingResult result = new NlpProcessingResult();
            result.setError(ProcessingError.builder().message(message).build());
            response.setResult(result);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // set the original footer to return it back to the client
        //
        response.getResult().setFooter(content.getContent().getFooter());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Processes the content - documents in bulk.
     */
    @PostMapping(value = apiFullPath + "/process_bulk")
    public ResponseEntity<ServiceBulkResponseContent> processBulk(@RequestBody /*@Valid*/ ServiceBulkRequestContent content) {

        ServiceBulkResponseContent response = new ServiceBulkResponseContent();

        // check whether we need to perform any processing
        //
        if (content.getContent() == null || content.getContent().isEmpty()) {
            final String message = "Empty content";
            NlpProcessingResult result = new NlpProcessingResult();
            result.setError(ProcessingError.builder().message(message).build());
            response.setResult(List.of(result));
            log.info(message);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // process the content
        //
        try {
            List<NlpProcessingResult> result = service.processBulk(content.getContent(), content.getApplicationParams());
            response.setResult(result);
        }
        catch (Exception e) {
            final String message = "Error processing the query: " + e.getMessage();
            log.error(message);

            NlpProcessingResult result = new NlpProcessingResult();
            result.setError(ProcessingError.builder().message(message).build());
            response.setResult(List.of(result));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // set the original footer to return it back to the client
        //
        assert response.getResult().size() == content.getContent().size();
        for (int i = 0; i < response.getResult().size(); ++i) {
            NlpProcessingResult res = response.getResult().get(i);
            NlpInputPayload ctx = content.getContent().get(i);
            res.setFooter(ctx.getFooter());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
