package eu.opertusmundi.api_auth.auth_subrequest.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.opertusmundi.api_auth.auth_subrequest.model.WfsRequest;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Named("publicWfsEndpointAuthorizer")
public class PublicWfsEndpointAuthorizer extends OwsAuthorizerBase implements Authorizer<WfsRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicWfsEndpointAuthorizer.class);
    
    @Override
    public Uni<Void> authorize(
        @NotNull @Valid AccountClientDto consumerAccountClient,
        @NotNull @Valid AccountDto providerAccount, 
        @NotBlank String requestId, 
        @NotNull @Valid WfsRequest request)
    {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("authorize(): [consumerAccountClient={} providerAccount={} requestId={} request={}]", 
                consumerAccountClient, providerAccount, requestId, request);
        }
        
        // TODO
        return Uni.createFrom().nullItem();
    }

}
