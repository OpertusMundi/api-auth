package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.opertusmundi.api_auth.auth_subrequest.model.OwsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.event.AuthorizationGrantedForWorkspaceResourceEvent;
import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountClientRequestRepository;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountClientRequestDto;
import eu.opertusmundi.api_auth.model.OwsServiceInfo;
import eu.opertusmundi.api_auth.model.WorkspaceInfo;


@ApplicationScoped
public class AuthorizationEventRecorder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationEventRecorder.class);
    
    @Inject
    AccountClientRequestRepository accountClientRequestRepository;
    
    @Inject
    LayerNamingStrategy layerNamingStrategy;
    
    @ConfigProperty(
        name = "eu.opertusmundi.api_auth.auth_subrequest.record-authorization-events", defaultValue = "true")
    boolean record = true;
    
    public void onAuthorizationGranted(@ObservesAsync AuthorizationGrantedForWorkspaceResourceEvent event)
    {
        final String requestId = event.getRequestId();
        final AccountClientDto consumerAccountClientDto = event.getConsumerAccountClient();
        final UUID clientKey = UUID.fromString(consumerAccountClientDto.getKey());
        
        final URL url = event.getOriginalUrl(); 
        final WorkspaceInfo workspaceInfo = event.getWorkspaceInfo();
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received AuthorizationGrantedForWorkspaceResourceEvent ["
                    + "requestId={}, consumerAccountClient.key={}, workspaceInfo={}]", 
                requestId, clientKey, workspaceInfo);
        }
       
        final var r = event.getRequest();
        
        final var b = AccountClientRequestDto.builder()
            .requestId(requestId) 
            .recorded(event.getWhen())
            .clientKey(clientKey)
            .hostname(url.getHost())
            .uri(event.getAuthRequestRedirect())
            .workspaceInfo(workspaceInfo);
        
        if (r instanceof OwsRequest) {
            final var o = (OwsRequest) r;
            // Extract asset keys from layer names
            final List<String> assetKeys = 
                layerNamingStrategy.extractAssetKeysFromLayerNames(o.getLayerNames());
            b.assetKeys(assetKeys.toArray(String[]::new));
            // Extract basic OWS service info
            b.owsServiceInfo(OwsServiceInfo.of(o.getService(), o.getVersion(), o.getRequest()));
        }
        
        final var dto = b.build();
        if (record) {
            accountClientRequestRepository.createFromDto(dto)
                .subscribe().with(
                    // success callback
                    Objects::requireNonNull, 
                    // failure callback
                    exception -> {
                        LOGGER.error("Failed to persist entity for AccountClientRequestEntity", exception);
                    });
        } else {
            LOGGER.info("About to persist entity from DTO (DRY-RUN): {}", dto);
        }
    }
}
