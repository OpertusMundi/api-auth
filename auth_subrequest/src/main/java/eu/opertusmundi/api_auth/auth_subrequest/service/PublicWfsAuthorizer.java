package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.opertusmundi.api_auth.auth_subrequest.model.WfsDescribeFeatureTypeRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WfsGetCapabilitiesRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WfsGetFeatureRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WfsRequest;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Named("publicWfsAuthorizer")
public class PublicWfsAuthorizer extends OwsAuthorizerBase implements Authorizer<WfsRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicWfsAuthorizer.class);
    
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
        
        final AccountDto consumerAccount = consumerAccountClient.getAccount();
        
        if (request instanceof WfsGetFeatureRequest) {
            final WfsGetFeatureRequest getFeatureRequest = (WfsGetFeatureRequest) request;
            final List<String> assetKeys = extractAssetKeysFromLayerNames(getFeatureRequest.getLayerNames());
            return checkAssetKeysFromSubscriptions(consumerAccount, providerAccount, assetKeys);
        } else if (request instanceof WfsDescribeFeatureTypeRequest) {
            final WfsDescribeFeatureTypeRequest describeFeatureTypeRequest = (WfsDescribeFeatureTypeRequest) request;
            final List<String> assetKeys = extractAssetKeysFromLayerNames(describeFeatureTypeRequest.getLayerNames());
            return checkAssetKeysFromSubscriptions(consumerAccount, providerAccount, assetKeys);
        } else if (request instanceof WfsGetCapabilitiesRequest) {
            // success (GetCapabilities is allowed for all consumers)
            return Uni.createFrom().nullItem();
        } else {
            // unsupported type of request
            return Uni.createFrom().failure(
                new IllegalStateException("unsupported type of WFS request: [" + request.getRequest() + "]" ));
        }
    }

}
