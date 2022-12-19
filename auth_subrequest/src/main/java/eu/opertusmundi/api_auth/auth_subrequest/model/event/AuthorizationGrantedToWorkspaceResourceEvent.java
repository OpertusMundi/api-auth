package eu.opertusmundi.api_auth.auth_subrequest.model.event;

import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;

import eu.opertusmundi.api_auth.auth_subrequest.model.Request;
import eu.opertusmundi.api_auth.auth_subrequest.model.WorkspaceInfo;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;

@lombok.Getter
public class AuthorizationGrantedToWorkspaceResourceEvent extends AuthorizationGrantedEvent
{
    @lombok.Builder
    AuthorizationGrantedToWorkspaceResourceEvent(
        ZonedDateTime when, String requestId, URL originalUrl, URI authRequestRedirect, Request request,
        WorkspaceInfo workspaceInfo, AccountClientDto consumerAccountClient, AccountDto providerAccount)
    {
        super(when, requestId, originalUrl, authRequestRedirect, request);
        this.workspaceInfo = workspaceInfo;
        this.consumerAccountClient = consumerAccountClient;
        this.providerAccount = providerAccount;
    }
    
    final WorkspaceInfo workspaceInfo;
    
    final AccountClientDto consumerAccountClient;
    
    final AccountDto providerAccount;

    @Override
    public String toString()
    {
        return String.format(
            "AuthorizationGrantedToWorkspaceResourceEvent ["
                + "workspaceInfo=%s, consumerAccountClient=%s, providerAccount=%s, "
                + "when=%s, requestId=%s, originalUrl=%s, authRequestRedirect=%s, request=%s]",
            workspaceInfo, consumerAccountClient, providerAccount, 
            when, requestId, originalUrl, authRequestRedirect, request);
    }
    
    
}
