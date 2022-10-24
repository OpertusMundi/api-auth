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
        System.err.printf(" === WmsAuthorizer.authorize():" +
            "\n\tconsumerClientKey=[%s]" +
            "\n\tproviderAccountKey=[%s]" +
            "\n\tr=%s\n", consumerClientKey, providerAccountKey, r);
    }
}
