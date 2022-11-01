package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import eu.opertusmundi.api_auth.model.AssetResourceDto;


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
            LOGGER.debug("authorize(): [\n\tconsumerAccountClient={}\n\tproviderAccount={}\n\trequestId={}\n\trequest={}]", 
                consumerAccountClient, providerAccount, requestId, request);
        }
        
        final int consumerAccountId = consumerAccountClient.getAccountId();
        final AccountDto consumerAccount = consumerAccountClient.getAccount();
        final int providerAccountId = providerAccount.getId();
        
        if (request instanceof WmsGetMapRequest) {
            final WmsGetMapRequest getMapRequest = (WmsGetMapRequest) request;
            final List<String> assetKeys = assetKeysFromLayerNames(getMapRequest.getLayerNames());
            return accountSubscriptionService.findByConsumerAndAssetKeys(consumerAccountId, assetKeys)
                .map(subscriptions -> {
                    final List<String> remainingAssetKeys = new ArrayList<>(assetKeys);
                    for (final AccountSubscriptionDto subscription: subscriptions) {
                        if (subscription.getProviderAccountId() == providerAccountId) {
                            final AssetResourceDto asset = Objects.requireNonNull(subscription.getAsset());
                            if (remainingAssetKeys.remove(asset.getKey())) {
                                if (remainingAssetKeys.isEmpty())
                                    break;
                            }
                        }
                    }
                    if (remainingAssetKeys.isEmpty()) {
                        return true; // success
                    } else {
                        throw new ConsumerNotAuthorizedForResourceException(consumerAccount.getKey(), remainingAssetKeys.get(0));
                    }
                })
                .replaceWithVoid();
        } else if (request instanceof WmsGetCapabilitiesRequest) {
            // success (GetCapabilities is allowed for all consumers)
            return Uni.createFrom().nullItem();
        } else if (request instanceof WmsGetLegendGraphicRequest) {
            final WmsGetLegendGraphicRequest getLegendGraphicRequest = (WmsGetLegendGraphicRequest) request;
            final String assetKey = assetKeyFromLayerName(getLegendGraphicRequest.getLayerName());
            return accountSubscriptionService.findByConsumerAndAssetKey(consumerAccountId, assetKey)
                .map(subscriptions -> {
                    for (final AccountSubscriptionDto subscription: subscriptions) {
                        if (subscription.getProviderAccountId() == providerAccountId)
                            return true; // success
                    }
                    // no suitable subscription found
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
