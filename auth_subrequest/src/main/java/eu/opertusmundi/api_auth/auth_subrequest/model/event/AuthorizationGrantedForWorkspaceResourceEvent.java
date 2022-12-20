package eu.opertusmundi.api_auth.auth_subrequest.model.event;

import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;

import eu.opertusmundi.api_auth.auth_subrequest.model.Request;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;
import eu.opertusmundi.api_auth.model.WorkspaceInfo;

@lombok.Getter
public class AuthorizationGrantedForWorkspaceResourceEvent extends AuthorizationGrantedEvent
{
    @lombok.Builder
    AuthorizationGrantedForWorkspaceResourceEvent(
        ZonedDateTime when, String requestId, URL originalUrl, URI authRequestRedirect, Request request,
        WorkspaceInfo workspaceInfo, AccountClientDto consumerAccountClient)
    {
        super(when, requestId, originalUrl, authRequestRedirect, request);
        this.workspaceInfo = workspaceInfo;
        this.consumerAccountClient = consumerAccountClient;
    }
    
    final WorkspaceInfo workspaceInfo;
    
    final AccountClientDto consumerAccountClient;
    
    @Override
    public String toString()
    {
        return String.format(
            "AuthorizationGrantedToWorkspaceResourceEvent ["
                + "when=%s, requestId=%s, originalUrl=%s, authRequestRedirect=%s, request=%s"
                + "workspaceInfo=%s, consumerAccountClient=%s]",
            when, requestId, originalUrl, authRequestRedirect, request, workspaceInfo, consumerAccountClient);
    }
    
    
}
