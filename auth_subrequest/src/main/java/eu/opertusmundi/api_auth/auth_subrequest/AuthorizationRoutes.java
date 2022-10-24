package eu.opertusmundi.api_auth.auth_subrequest;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.vertx.web.Header;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.Route.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import static eu.opertusmundi.api_auth.auth_subrequest.ExtraHttpHeaders.*;

@ApplicationScoped
public class AuthorizationRoutes
{    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationRoutes.class);
    
    @Route(methods = HttpMethod.GET, path = "/authorize", produces = MediaType.TEXT_PLAIN) 
    void authorize(RoutingContext routingContext,
        @Header(REQUEST_ID_HEADER_NAME) String requestId,
        @Header(AUTH_REQUEST_REDIRECT_HEADER_NAME) Optional<String> optionalAuthRequestRedirect) 
    { 
        final String authRequestPath = optionalAuthRequestRedirect.map(URI::create)
            .map(URI::getPath).orElse(null);
        
        if (StringUtils.isEmpty(authRequestPath) || !authRequestPath.startsWith("/")) {
            routingContext.response().setStatusCode(RestResponse.StatusCode.BAD_REQUEST).end();
        } else {
            final String pathToReroute = "/authorize" + authRequestPath;
            LOGGER.info("Rerouting to {} [req-id={}]", pathToReroute, requestId);
            routingContext.reroute(pathToReroute);
        }
    }

}
