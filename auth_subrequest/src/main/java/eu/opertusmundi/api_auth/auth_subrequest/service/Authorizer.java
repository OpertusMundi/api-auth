package eu.opertusmundi.api_auth.auth_subrequest.service;

import eu.opertusmundi.api_auth.auth_subrequest.model.BaseRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.ConsumerNotAuthorizedException;

public interface Authorizer <R extends BaseRequest>
{
    public void authorize(String consumerClientKey, String providerAccountKey, R r)
        throws ConsumerNotAuthorizedException;
}
