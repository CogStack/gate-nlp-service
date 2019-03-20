package nlp.gateway.model.protocol;

import lombok.Data;
import java.util.Map;

// TODO: each application should refine this one


@Data
public class ServiceArguments {
    Map<String, String> args;
}
