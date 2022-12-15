package eu.opertusmundi.api_auth.auth_subrequest.service.public_ogc;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.model.WmsDescribeLayerRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetCapabilitiesRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetLegendGraphicRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetMapRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.service.Authorizer;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;


@ApplicationScoped
@Named("public_ogc.WmsAuthorizer")
public class WmsAuthorizer extends SubscriptionBasedOwsAuthorizerSupport 
    implements Authorizer<WmsRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WmsAuthorizer.class);

    @Override
    public Uni<Void> authorize(
        @NotNull @Valid AccountClientDto consumerAccountClient, 
        @NotNull @Valid AccountDto providerAccount, 
        @NotBlank String requestId, 
        @NotNull @Valid WmsRequest request)
    {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("authorize(): [consumerAccountClient={} providerAccount={} requestId={} request={}]", 
                consumerAccountClient, providerAccount, requestId, request);
        }
        
        final AccountDto consumerAccount = consumerAccountClient.getAccount();
        
        if (request instanceof WmsGetMapRequest) {
            final WmsGetMapRequest getMapRequest = (WmsGetMapRequest) request;
            final List<String> assetKeys = 
                layerNamingStrategy.extractAssetKeysFromLayerNames(getMapRequest.getLayerNames());
            return checkAssetKeysFromSubscriptions(consumerAccount, providerAccount, assetKeys);
        } else if (request instanceof WmsGetCapabilitiesRequest) {
            // success (GetCapabilities is allowed for all consumers)
            return Uni.createFrom().nullItem();
        } else if (request instanceof WmsGetLegendGraphicRequest) {
            final WmsGetLegendGraphicRequest getLegendGraphicRequest = (WmsGetLegendGraphicRequest) request;
            final String assetKey = 
                layerNamingStrategy.extractAssetKeyFromLayerName(getLegendGraphicRequest.getLayerName());
            return checkAssetKeyFromSubscriptions(consumerAccount, providerAccount, assetKey);
        } else if (request instanceof WmsDescribeLayerRequest) {
            // success (DescribeLayer is allowed for all consumers)
            return Uni.createFrom().nullItem();
        } else {
            // unsupported type of request
            return Uni.createFrom().failure(
                new IllegalStateException("unsupported type of WMS request: [" + request.getRequest() + "]" ));
        }
    }
}
