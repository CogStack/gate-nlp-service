package nlp.gateway.controller;

import nlp.gateway.config.ServiceDefinition;
import nlp.gateway.config.GatewayConfig;
import nlp.common.model.protocol.ServiceSingleRequestContent;
import nlp.common.model.protocol.ServiceSingleResponseContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import java.util.Map;


/**
 * Gateway REST controller implementing data exchange between the client and
 * requested available NLP service.
 */
@RestController
public class GatewayController {

    /**
     * Gateway configuration.
     */
    @Autowired
    GatewayConfig gatewaConfig;

    @Autowired
    RestTemplate restTemplate;


    /**
     * Definitions of available services.
     */
    @Autowired
    @Qualifier("servicesDefinition")
    Map<String, ServiceDefinition> servicesDefinition;


    /**
     * Gateway endpoint configuration
     * TODO: specify in the Gateway configuration file
     */
    private final String apiPathPrefix = "/**/api";
    //private final String apiVersion = "v1";
    private final String apiFullPath = apiPathPrefix;


    private Logger log = LoggerFactory.getLogger(GatewayController.class);


    @PostConstruct
    public void init() {
        if (servicesDefinition.size() == 0) {
            log.warn("No NLP services definitions found in the gateway configuration.");
        }
    }

    /**
     * Endpoint for querying for information about particular NLP service.
     */
    @RequestMapping(value = apiFullPath + "/{applicationName}/info", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> info(@PathVariable String applicationName) {

        // check whether the application exists
        //
        log.info("Accessing /info endpoint for '" + applicationName + "' using HTTP GET");

        if (!servicesDefinition.containsKey(applicationName)) {
            log.error("Requested application: '" + applicationName + "' does not exist.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // send the processing request to an external nlp service
        //
        Map<String, Object> serviceInfo;
        try {
            serviceInfo = this.queryServiceForInfo(applicationName, "/info");
        }
        catch (RuntimeException e) {
            log.error("Error processing request for application: '" + applicationName + "'.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // TODO:
        // - validate the result


        // pack and return the results
        //
        return new ResponseEntity<>(serviceInfo, HttpStatus.OK);
    }

    /**
     * Endpoint for query the NLP application to process the content.
     */
    @RequestMapping(value = apiFullPath + "/{applicationName}/process", method=RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<ServiceSingleResponseContent> process(@PathVariable String applicationName,
                                                                @RequestBody /*@Valid*/ ServiceSingleRequestContent content) {
        // check whether the application exists
        //
        log.info("Accessing /process endpoint for '" + applicationName + "' using HTTP POST");

        if (!servicesDefinition.containsKey(applicationName)) {
            log.error("Requested application: '" + applicationName + "' does not exist.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // process the nlp by sending the request to external service
        //
        ServiceSingleResponseContent response;
        try {
            response = this.queryServiceForProcessing(applicationName, "/process", content);
        }
        catch (RuntimeException e) {
            log.error("Error processing request for application: '" + applicationName + "'.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // TODO:
        // - validate the result

        // pack and return the results
        //
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * A helper method to query the NLP service for the service information
     */
    private Map<String, Object> queryServiceForInfo(String applicationName, String endpointPath) throws RuntimeException {
        ServiceDefinition app = servicesDefinition.get(applicationName);

        String appUrl = app.getEndpoint() + endpointPath;
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(appUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<Map<String, Object>>() {
                });
        return responseEntity.getBody();
    }

    /**
     * A helper method to query the NLP service to process the content
     */
    private ServiceSingleResponseContent queryServiceForProcessing(String applicationName,
                                                                   String endpointPath,
                                                                   ServiceSingleRequestContent content) throws RuntimeException {
        ServiceDefinition app = servicesDefinition.get(applicationName);

        String appUrl = app.getEndpoint() + endpointPath;
        ResponseEntity<ServiceSingleResponseContent> responseEntity = restTemplate.exchange(appUrl, HttpMethod.POST,
                new HttpEntity<>(content),
                new ParameterizedTypeReference<ServiceSingleResponseContent>() {
                });
        return responseEntity.getBody();
    }

}
