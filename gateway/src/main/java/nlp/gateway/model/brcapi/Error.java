package nlp.gateway.model.brcapi;

import lombok.Data;


@Data
public class Error {
    Integer code;
    String message;
    String description;
}
