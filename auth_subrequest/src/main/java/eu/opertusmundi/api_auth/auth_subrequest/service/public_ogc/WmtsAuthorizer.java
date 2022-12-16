package eu.opertusmundi.api_auth.auth_subrequest.service.public_ogc;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.opertusmundi.api_auth.auth_subrequest.model.WmtsGetCapabilitiesRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmtsGetTileRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmtsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.service.Authorizer;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Named("public_ogc.WmtsAuthorizer")
public class WmtsAuthorizer extends SubscriptionBasedOwsAuthorizerSupport 
    implements Authorizer<WmtsRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WmtsAuthorizer.class);
    
    @Override
    public Uni<Void> authorize(
        @NotNull @Valid AccountClientDto consumerAccountClient, 
        @NotNull @Valid AccountDto providerAccount, 
        @NotBlank String requestId, 
        @NotNull @Valid WmtsRequest request)
    {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("authorize(): [consumerAccountClient={} providerAccount={} requestId={} request={}]", 
                consumerAccountClient, providerAccount, requestId, request);
        }
        
        final AccountDto consumerAccount = consumerAccountClient.getAccount();
        if (consumerAccount.getId().equals(providerAccount.getId())) {
            // success (consumer is same with provider)
            return Uni.createFrom().nullItem();
        }
        
        if (request instanceof WmtsGetTileRequest) {
            final WmtsGetTileRequest getTileRequest = (WmtsGetTileRequest) request;
            final String assetKey = 
                layerNamingStrategy.extractAssetKeyFromLayerName(getTileRequest.getLayerName());
            return checkAssetKeyFromSubscriptions(consumerAccount, providerAccount, assetKey);
        } else if (request instanceof WmtsGetCapabilitiesRequest) {
            // success (GetCapabilities is allowed for all consumers)
            return Uni.createFrom().nullItem();
        } else {
            // unsupported type of request
            return Uni.createFrom().failure(
                new IllegalStateException("unsupported type of WMTS request: [" + request.getRequest() + "]" ));
        }
    }
}
