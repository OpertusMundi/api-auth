package eu.opertusmundi.api_auth.auth_subrequest.model;

public class ConsumerNotAuthorizedException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ConsumerNotAuthorizedException(String consumerAccountKey, String assetKey)
    {
        super(String.format("consumer [%s] is not authorized to access [%s]", 
            consumerAccountKey, assetKey));
    }
}
