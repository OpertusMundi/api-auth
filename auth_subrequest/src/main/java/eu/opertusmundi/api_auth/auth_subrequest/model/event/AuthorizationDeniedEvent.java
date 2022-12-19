package eu.opertusmundi.api_auth.auth_subrequest.model.event;

import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;

import eu.opertusmundi.api_auth.auth_subrequest.model.Request;

@lombok.Getter
public class AuthorizationDeniedEvent extends AuthorizationEvent
{
    AuthorizationDeniedEvent(
        ZonedDateTime when, String requestId, URL originalUrl, URI authRequestRedirect, Request request, 
        String reason)
    {
        super(when, requestId, originalUrl, authRequestRedirect, request);
        this.reason = reason;
    }
    
    final String reason;
    
    public static AuthorizationDeniedEvent of(
        ZonedDateTime when, String requestId, URL originalUrl, URI authRequestRedirect, Request request, 
        String reason)
    {
        return new AuthorizationDeniedEvent(when, requestId, originalUrl, authRequestRedirect, request, reason);
    }

    @Override
    public String toString()
    {
        return String.format(
            "AuthorizationDeniedEvent ["
                + "reason=%s, when=%s, requestId=%s, originalUrl=%s, authRequestRedirect=%s, request=%s]",
            reason, when, requestId, originalUrl, authRequestRedirect, request);
    }
}
