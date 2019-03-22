package nlp.service.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * NLP service-specific configuration. Parameters are set only once during the
 * service initialization, cannot be modified during runtime.
 */
@Data
@Configuration
public class ServiceConfiguration {

    /**
     * General application details.
     */
    @Value("${application.name:#{null}}")
    @JsonProperty("name")
    String appName;

    @Value("${application.version:#{null}}")
    @JsonProperty("version")
    String appVersion;

    @Value("${application.language:#{null}}")
    @JsonProperty("lang")
    String appLanguage;

    /**
     * The application-specific parameters.
     */
    @Value("#{${application.params}}")
    @JsonProperty("params")
    Map<String, Object> appParams;

    /**
     * The name of the Java class implementing the NlpService interface
     */
    @Value("${application.class.name}")
    @JsonIgnore
    String appClassName;

    public ApplicationConfiguration getAppConfig() {
        return ApplicationConfiguration.builder()
                .name(appName)
                .version(appVersion)
                .language(appLanguage)
                .parameters(appParams)
                .build();
    }
}
