package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.model.ConsumerNotAuthorizedException;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsDescribeLayerRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetCapabilitiesRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetLegendGraphicRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetMapRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsRequest;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;


@ApplicationScoped
public class WmsAuthorizer extends OwsAuthorizerBase implements Authorizer<WmsRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WmsAuthorizer.class);

    @Override
    public Uni<Void> authorize(
        @NotNull AccountClientDto consumerAccountClient, 
        @NotNull AccountDto providerAccount, 
        @NotBlank String requestId, @NotNull WmsRequest request)
    {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("authorize(): consumerAccountClient={}, providerAccount={} requestId={} request={}", 
                consumerAccountClient, providerAccount, requestId, request);
        }
        
        // Todo
        
        if (request instanceof WmsGetMapRequest) {
            // Todo
        } else if (request instanceof WmsGetCapabilitiesRequest) {
            // success (GetCapabilities is allowed for all consumers)
            return Uni.createFrom().nullItem();
        } else if (request instanceof WmsGetLegendGraphicRequest) {
            // Todo
        } else if (request instanceof WmsDescribeLayerRequest) {
            // success (DescribeLayer is allowed for all consumers)
            return Uni.createFrom().nullItem();
        } else {
            // unsupported type of request
            return Uni.createFrom().failure(
                new IllegalStateException("unknown type of WMS request: [" + request.getRequest() + "]" ));
        }
        
        // Fixme
        return Uni.createFrom().nullItem();
        //return Uni.createFrom().failure(new IllegalStateException("aaaaaaaargggggggg!!!"));
        //return Uni.createFrom().failure(new ConsumerNotAuthorizedException("CONSUMER-ACCOUNT-KEY", "ASSET-KEY"));
    }
    
    
}
