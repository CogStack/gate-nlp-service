package nlp.gateway.config;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

/**
 * Definition of a NLP service used by the gateway.
 */
@Data
@Builder
public class ServiceDefinition {
    String name;
    String version;
    String endpoint;

    /**
     * Defines how the names of the fields returned as KVPs by the NLP service
     * should be re-named for compatibility with downstream application.
     */
    Map<String, String> fieldsMapping;
}
