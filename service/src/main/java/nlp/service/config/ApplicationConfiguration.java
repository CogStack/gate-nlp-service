package nlp.service.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


/**
 * NLP service-specific configuration. Parameters are set only once during the
 * service initialization, cannot be modified during runtime.
 */
@Data
@Configuration
public class ApplicationConfiguration {
    /**
     * General application details.
     */
    @Value("${application.name:#{null}}")
    @JsonProperty("name")
    @JsonView(JsonPropertyAccessView.Public.class)
    String appName;

    @Value("${application.version:#{null}}")
    @JsonProperty("version")
    @JsonView(JsonPropertyAccessView.Public.class)
    String appVersion;

    @Value("${application.language:#{null}}")
    @JsonProperty("lang")
    @JsonView(JsonPropertyAccessView.Public.class)
    String appLanguage;

    @Value("${application.endpoint.single-doc.fail-on-empty-content:true}")
    @JsonProperty("single_doc_endpoint_fail_on_empty_content")
    @JsonView(JsonPropertyAccessView.Public.class)
    boolean appSingleDocEndpointFailOnEmptyContent;


    /**
     * The application-specific parameters.
     */
    @Value("#{${application.params}}")
    @JsonProperty("params")
    @JsonView(JsonPropertyAccessView.Public.class)
    Map<String, Object> appParams;

    /**
     * The name of the Java class implementing the NlpService interface
     */
    @Value("${application.class.name}")
    @JsonIgnore
    String appClassName;
}
