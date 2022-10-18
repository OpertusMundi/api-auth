package eu.opertusmundi.api_auth.auth_subrequest;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.service.AccountClientService;

@Authenticated
@Path("/authorize")
public class AuthorizationController
{
    public static final String REQUEST_REDIRECT_HEADER_NAME = "x-auth-request-redirect"; 
    
    public static final String REQUEST_ID_HEADER_NAME = "x-request-id";
    
    public static final String ORIG_URL_HEADER_NAME = "x-original-url";
    
    public static final String ORIG_METHOD_HEADER_NAME = "x-original-method";
    
    public static final String REMOTE_USER_HEADER_NAME = "x-remote-user";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationController.class);
    
    @Inject
    SecurityIdentity securityIdentity;
    
    @Inject
    AccountClientService accountClientService;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<Object>> authorize(
        @HeaderParam(REQUEST_ID_HEADER_NAME) final String requestId,
        @HeaderParam(REQUEST_REDIRECT_HEADER_NAME) final URI authRequestRedirect, 
        @HeaderParam(ORIG_METHOD_HEADER_NAME) final String origMethod,
        @HeaderParam(ORIG_URL_HEADER_NAME) final URL origUrl)
    {
        final JsonWebToken jwt = (JsonWebToken) securityIdentity.getPrincipal();
        final String principalName = jwt.getName();
        
        final String clientId = jwt.getClaim("clientId");
        if (clientId == null || clientId.isBlank()) {
            return Uni.createFrom().item(RestResponse.status(Status.FORBIDDEN, 
                "claim `clientId` is missing from JWT token"));
        }
        
        if (!clientId.equals(jwt.getClaim(Claims.azp))) {
            return Uni.createFrom().item(RestResponse.status(Status.FORBIDDEN, 
                "azp is different from clientId"));
        }
        
        if (authRequestRedirect == null || 
                authRequestRedirect.getHost() != null || authRequestRedirect.getScheme() != null) {
            return Uni.createFrom().item(RestResponse.status(Status.BAD_REQUEST, 
                "authRequestRedirect is expected as a root-relative URI"));
        }
        
        LOGGER.info("Checking authorization of [{}] for: {} {}", principalName, origMethod, authRequestRedirect);
        
        final String authRequestRedirectPath = authRequestRedirect.getPath();
        final Map<String, String> authRequestRedirectQuery = Optional.ofNullable(authRequestRedirect.getQuery())
            .map(this::parseQueryStringToMap).orElseGet(Collections::emptyMap);
        
        return accountClientService.fetch(clientId)
            .map(accountClient -> {
                // TODO
                LOGGER.info(" == accountClient.clientId={}", accountClient.getClientId());
                LOGGER.info(" == accountClient.accountDto={}", accountClient.getAccount());
                
                return RestResponse.ResponseBuilder.noContent()
                    .header(REMOTE_USER_HEADER_NAME, principalName)
                    .build();
            })
            //.onFailure() // TODO
            ;
    }
    
    private Map<String, String> parseQueryStringToMap(String query)
    {
        return Arrays.stream(StringUtils.split(query, '&'))
            .map(s -> StringUtils.split(s, "=", 2))
            .collect(Collectors.toUnmodifiableMap(p -> p[0], p -> p.length < 2? "" : p[1], (x, y) -> y));
    }
}
