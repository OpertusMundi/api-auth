package eu.opertusmundi.rest_auth;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.opertusmundi.rest_auth.model.AccountClientDto;
import eu.opertusmundi.rest_auth.service.AccountClientService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;


@Path("/authorize")
public class AuthorizationController
{
    public static final String REQUEST_REDIRECT_HEADER_NAME = "x-auth-request-redirect"; 
    
    public static final String REQUEST_ID_HEADER_NAME = "x-request-id";
    
    public static final String ORIG_URL_HEADER_NAME = "x-original-url";
    
    public static final String ORIG_METHOD_HEADER_NAME = "x-original-method";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationController.class);
    
    @Inject
    SecurityIdentity securityIdentity;
    
    @Inject
    AccountClientService accountClientService;
    
    @Authenticated
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<Object>> authorize(
        @HeaderParam(REQUEST_ID_HEADER_NAME) final String requestId,
        @HeaderParam(REQUEST_REDIRECT_HEADER_NAME) final String authRequestRedirect, 
        @HeaderParam(ORIG_METHOD_HEADER_NAME) final String origMethod,
        @HeaderParam(ORIG_URL_HEADER_NAME) final URL origUrl) 
            throws MalformedURLException
    {
        final JsonWebToken jwt = (JsonWebToken) securityIdentity.getPrincipal();
        final String principalName = jwt.getName();
        
        final String clientId = jwt.getClaim("clientId");
        if (clientId == null) {
            return Uni.createFrom().item(RestResponse.status(Status.FORBIDDEN));
        }
        
        if (authRequestRedirect == null || !authRequestRedirect.startsWith("/")) {
            return Uni.createFrom().item(RestResponse.status(Status.BAD_REQUEST, 
                "authRequestRedirect is expected as an absolute path (starting with slash)"));
        }
        
        final String authRequestRedirectPath = (new URL("http", "localhost", authRequestRedirect)).getPath();
        LOGGER.info("Checking if [{}] authorized for: {} {}", principalName, origMethod, authRequestRedirectPath);
        
        return accountClientService.fetch(clientId)
            .map(accountClient -> {
                // TODO
                LOGGER.info(" == accountClient={}", accountClient);
                return RestResponse.noContent();
            })
            .onFailure()
    }
    
    
}
