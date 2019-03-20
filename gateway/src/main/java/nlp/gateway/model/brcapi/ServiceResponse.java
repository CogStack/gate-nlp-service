package nlp.gateway.model.brcapi;

import lombok.Data;


// TODO: make compatible with service

@Data
public class ServiceResponse {

    @Data
    public class ServerInfo {
        String name;
        String version;
    }

    // TODO: make as ENUM
    Integer status;

    Error errors;

    ProtocolDescription protocol;

    ServerInfo serverInfo;
}
