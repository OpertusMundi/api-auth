package eu.opertusmundi.api_auth.auth_subrequest.model.exception;

import java.util.UUID;

public class ConsumerNotAuthorizedForResourceException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    @lombok.Getter
    private final UUID consumerAccountKey;
    
    @lombok.Getter
    private final String assetKey;
    
    public ConsumerNotAuthorizedForResourceException(UUID consumerAccountKey, String assetKey)
    {
        super(String.format("consumer [%s] is not authorized to access resource [%s]", 
            consumerAccountKey, assetKey));
        this.consumerAccountKey = consumerAccountKey;
        this.assetKey = assetKey;
    }
}
