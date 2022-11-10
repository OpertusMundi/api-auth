package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.model.ConsumerNotAuthorizedForResourceException;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsDescribeLayerRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetCapabilitiesRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetLegendGraphicRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetMapRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsRequest;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;
import eu.opertusmundi.api_auth.model.AccountSubscriptionDto;


@ApplicationScoped
@Named("publicWmsEndpointAuthorizer")
public class PublicWmsEndpointAuthorizer extends OwsAuthorizerBase implements Authorizer<WmsRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicWmsEndpointAuthorizer.class);

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
        
        final int consumerAccountId = consumerAccountClient.getAccountId();
        final AccountDto consumerAccount = consumerAccountClient.getAccount();
        final int providerAccountId = providerAccount.getId();
        
        if (request instanceof WmsGetMapRequest) {
            final WmsGetMapRequest getMapRequest = (WmsGetMapRequest) request;
            final List<String> assetKeys = assetKeysFromLayerNames(getMapRequest.getLayerNames());
            return accountSubscriptionService.findByConsumerAndProviderAndAssetKeys(consumerAccountId, providerAccountId, assetKeys)
                .invoke(subscriptions -> {
                    final List<String> assetKeysFromSubscriptions = subscriptions.stream()
                        .map(AccountSubscriptionDto::getAssetKey).collect(Collectors.toList());
                    for (final String assetKey: assetKeys) {
                        if (!assetKeysFromSubscriptions.contains(assetKey))
                            throw new ConsumerNotAuthorizedForResourceException(consumerAccount.getKey(), assetKey);
                    }
                })
                .replaceWithVoid();
        } else if (request instanceof WmsGetCapabilitiesRequest) {
            // success (GetCapabilities is allowed for all consumers)
            return Uni.createFrom().nullItem();
        } else if (request instanceof WmsGetLegendGraphicRequest) {
            final WmsGetLegendGraphicRequest getLegendGraphicRequest = (WmsGetLegendGraphicRequest) request;
            final String assetKey = assetKeyFromLayerName(getLegendGraphicRequest.getLayerName());
            return accountSubscriptionService.findByConsumerAndProviderAndAssetKey(consumerAccountId, providerAccountId, assetKey)
                .invoke(subscriptions -> {
                    if (subscriptions.isEmpty())
                        throw new ConsumerNotAuthorizedForResourceException(consumerAccount.getKey(), assetKey);
                })
                .replaceWithVoid();
        } else if (request instanceof WmsDescribeLayerRequest) {
            // success (DescribeLayer is allowed for all consumers)
            return Uni.createFrom().nullItem();
        } else {
            // unsupported type of request
            return Uni.createFrom().failure(
                new IllegalStateException("unknown type of WMS request: [" + request.getRequest() + "]" ));
        }
    }
}
