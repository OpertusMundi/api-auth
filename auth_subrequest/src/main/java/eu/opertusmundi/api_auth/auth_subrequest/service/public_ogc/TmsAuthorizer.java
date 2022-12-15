package eu.opertusmundi.api_auth.auth_subrequest.service.public_ogc;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.opertusmundi.api_auth.auth_subrequest.model.TmsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.service.Authorizer;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Named("public_ogc.TmsAuthorizer")
public class TmsAuthorizer extends SubscriptionBasedOwsAuthorizerSupport 
    implements Authorizer<TmsRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TmsAuthorizer.class);
    
    @Override
    public Uni<Void> authorize(
        @NotNull @Valid AccountClientDto consumerAccountClient,
        @NotNull @Valid AccountDto providerAccount,
        @NotBlank String requestId, 
        @NotNull @Valid TmsRequest request)
    {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("authorize(): [consumerAccountClient={} providerAccount={} requestId={} request={}]", 
                consumerAccountClient, providerAccount, requestId, request);
        }
        
        final AccountDto consumerAccount = consumerAccountClient.getAccount();
        final String assetKey = layerNamingStrategy.extractAssetKeyFromLayerName(request.getLayerName());
        return checkAssetKeyFromSubscriptions(consumerAccount, providerAccount, assetKey);
    }
}
