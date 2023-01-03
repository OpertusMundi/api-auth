package eu.opertusmundi.api_auth.auth_subrequest.model.exception;

public class AccountClientIsRevokedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    @lombok.Getter
    private final String clientKey;
    
    public AccountClientIsRevokedException(String clientKey)
    {
        super("client is revoked [key=" + clientKey + "]");
        this.clientKey = clientKey;
    }
}
