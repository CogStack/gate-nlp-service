package nlp.gateway.config;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;


/**
 * Configuration of NLP services by the gateway.
 */
@Configuration
public class ServicesConfig {

    /**
     * Specifies the folder in which the services definition files are located
     */
    @Value("${services.location}")
    private String servicesLocation;

    /**
     * A list of enabled services to be used by the gateway instance
     */
    @Value("${services.enabled}")
    private List<String> servicesEnabled;


    /**
     * Bean containing the definitions of all enabled and available services.
     */
    @Bean
    @Qualifier("servicesDefinition")
    Map<String, ServiceDefinition> servicesDefinition() {
        Map<String, ServiceDefinition> endpoints = new HashMap<>();

        Map<String, Properties> serviceProperties = loadAllServiceProperties();
        serviceProperties.entrySet().forEach(entry -> {
            Properties endpointProps = entry.getValue();
            endpoints.put(entry.getKey(), ServiceDefinition.builder()
                    .name(endpointProps.getProperty("name"))
                    .version(endpointProps.getProperty("version"))
                    .endpoint(endpointProps.getProperty("endpoint"))
                    .build());

            // TODO: handle mapping
        });

        return endpoints;
    }


    private Map<String, Properties> loadAllServiceProperties() {
        return servicesEnabled.stream()
                .collect(toMap(filename -> filename, this::loadProperties));
    }


    private Properties loadProperties(String filename) {
        Resource[] possiblePropertiesResources = {
                new ClassPathResource(filename),
                new PathResource(filename),
                new PathResource(getPropertiesFilePath(filename))
        };
        Optional<Resource> availableResource = stream(possiblePropertiesResources)
                .filter(Resource::exists)
                .reduce((previous, current) -> current);

        if (!availableResource.isPresent()) {
            throw new RuntimeException("No resources available for provided filename: " + filename);
        }
        Resource resource = availableResource.get();

        Properties properties = new Properties();
        try {
            properties.load(resource.getInputStream());
        } catch(IOException exception) {
            throw new RuntimeException(exception);
        }

        return properties;
    }


    private Path getPropertiesFilePath(String serviceName) {
        return serviceName.endsWith(".properties")
                ? Paths.get(serviceName) : Paths.get(servicesLocation , serviceName + ".properties");
    }
}
