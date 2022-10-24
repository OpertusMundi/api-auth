package eu.opertusmundi.api_auth.auth_subrequest.service;

import javax.enterprise.context.ApplicationScoped;

import eu.opertusmundi.api_auth.auth_subrequest.model.ConsumerNotAuthorizedException;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsRequest;

@ApplicationScoped
public class WmsAuthorizer implements Authorizer<WmsRequest>
{
    @Override
    public void authorize(String consumerClientKey, String providerAccountKey, WmsRequest r)
        throws ConsumerNotAuthorizedException
    {
        // Todo Auto-generated method stub
        System.err.println(" === WmsAuthorizer.authorize():");
        System.err.println(" ===    consumerClientKey=" + consumerClientKey);
        System.err.println(" ===    providerAccountKey=" + providerAccountKey);
        System.err.println(" ===    r=" + r);    
    }
}
