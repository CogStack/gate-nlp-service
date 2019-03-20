package nlp.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;


/**
 * The configuration of the NLP application that will be exposed to the API.
 */
@Data
public class ApplicationConfiguration {

    @JsonProperty("name")
    String name;

    @JsonProperty("version")
    String version;

    @JsonProperty("lang")
    String language;

    /**
     * Application-specific parameters.
     */
    @JsonProperty("params")
    Map<String, Object> parameters;
}
