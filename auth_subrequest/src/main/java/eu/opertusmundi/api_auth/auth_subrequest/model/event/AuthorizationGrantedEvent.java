package eu.opertusmundi.api_auth.auth_subrequest.model.event;

import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;

import eu.opertusmundi.api_auth.auth_subrequest.model.Request;

@lombok.Getter
public class AuthorizationGrantedEvent extends AuthorizationEvent
{
    AuthorizationGrantedEvent(
        ZonedDateTime when, String requestId, URL originalUrl, URI authRequestRedirect, Request request)
    {
        super(when, requestId, originalUrl, authRequestRedirect, request);
    }
    
    public static AuthorizationGrantedEvent of(
        ZonedDateTime when, String requestId, URL originalUrl, URI authRequestRedirect, Request request)
    {
        return new AuthorizationGrantedEvent(when, requestId, originalUrl, authRequestRedirect, request); 
    }

    @Override
    public String toString()
    {
        return String.format(
            "AuthorizationGrantedEvent ["
                + "when=%s, requestId=%s, originalUrl=%s, authRequestRedirect=%s, request=%s]",
            when, requestId, originalUrl, authRequestRedirect, request);
    }
    
    
}
