package eu.opertusmundi.api_auth.auth_subrequest.model.event;

import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;

import eu.opertusmundi.api_auth.auth_subrequest.model.Request;
import lombok.AccessLevel;

@lombok.Getter
@lombok.RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizationEvent
{
    final ZonedDateTime when;
    
    final String requestId;
    
    final URL originalUrl;
    
    final URI authRequestRedirect;
    
    final Request request;
}
