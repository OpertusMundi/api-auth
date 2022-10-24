package eu.opertusmundi.api_auth.auth_subrequest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.opertusmundi.api_auth.auth_subrequest.model.ConsumerNotAuthorizedException;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.service.Authorizer;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.opertusmundi.api_auth.auth_subrequest.util.QueryStringUtil.*;
import static eu.opertusmundi.api_auth.auth_subrequest.ExtraHttpHeaders.*;

/**
 * Authorize requests for OGC services in the workspace of a provider user.
 */
@Authenticated
@Path("/authorize/{workspace:[_a-z][-_a-z0-9]*}")
public class WorkspaceAuthorizationController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceAuthorizationController.class);
    
    static final Pattern WORKSPACE_PATTERN = Pattern.compile(
        "(?<workspace>" +
            "(?<workspacePrefix>[a-z][-a-z0-9]*)?" + "_" +
            "(?<providerAccountKey>[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})" + ")", 
        Pattern.CASE_INSENSITIVE);
    
    static class WorkspaceInfo
    {
        final String prefix;
        
        final String providerAccountKey;
        
        WorkspaceInfo(String prefix, String providerAccountKey)
        {
            this.prefix = prefix;
            this.providerAccountKey = Objects.requireNonNull(providerAccountKey);
        }
        
        static WorkspaceInfo fromString(String workspace)
        {
            final Matcher workspaceMatcher = WORKSPACE_PATTERN.matcher(workspace);
            if (!workspaceMatcher.matches()) {
                throw new IllegalArgumentException("workspace is malformed: [" + workspace + "]");
            } 
            return new WorkspaceInfo(
                workspaceMatcher.group("workspacePrefix"), 
                workspaceMatcher.group("providerAccountKey"));
        }
    }
    
    @Context
    SecurityIdentity securityIdentity;

    @Context
    HttpHeaders httpHeaders;
    
    @Context
    UriInfo uriInfo;
    
    @Inject
    Authorizer<WmsRequest> wmsAuthorizer;
    
    @GET
    @Path("/wms")
    public Uni<RestResponse<?>> authorizeForWms(
        @HeaderParam(AUTH_REQUEST_REDIRECT_HEADER_NAME) final URI authRequestRedirect)
    {
        final String clientKey = clientKeyFromContext();
        final WorkspaceInfo workspaceInfo = workspaceInfoFromContext();
        final WmsRequest wmsRequest = WmsRequest.fromMap(parseQueryStringToMap(authRequestRedirect));
        try {
            wmsAuthorizer.authorize(clientKey, workspaceInfo.providerAccountKey, wmsRequest);
        } catch (ConsumerNotAuthorizedException x) {
            return Uni.createFrom().item(responseForNotAuthorized(x.getMessage()));
        }
        return Uni.createFrom().item(responseForSuccess()); 
    }
    
    private String clientKeyFromContext()
    {
        final JsonWebToken jwt = (JsonWebToken) securityIdentity.getPrincipal();
        
        final String clientKey = jwt.getClaim("clientId");
        Validate.validState(!StringUtils.isBlank(clientKey), 
            "claim [clientId] is missing from JWT token");
        Validate.validState(clientKey.equals(jwt.getClaim(Claims.azp)), 
            "claim [clientId] is different from claim [azp]");
        
        return clientKey;
    }
    
    private WorkspaceInfo workspaceInfoFromContext()
    {
        final MultivaluedMap<String, String> pathParameters = uriInfo.getPathParameters();
        return WorkspaceInfo.fromString(pathParameters.getFirst("workspace"));
    }
    
    private String requestIdFromContext()
    {
        return this.httpHeaders.getHeaderString(REQUEST_ID_HEADER_NAME);
    }
    
    private RestResponse<Object> responseForBadRequest(String message)
    {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST)
            .header(ERROR_MESSAGE_HEADER_NAME, message)
            .build();
    }
    
    private RestResponse<Object> responseForNotAuthorized(String message)
    {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.FORBIDDEN)
            .header(ERROR_MESSAGE_HEADER_NAME, message)
            .build();
    }
    
    private RestResponse<Object> responseForSuccess()
    {
        return RestResponse.ResponseBuilder.noContent()
            .header(REMOTE_USER_HEADER_NAME, securityIdentity.getPrincipal().getName())
            .build();
    }
    
    @ServerExceptionMapper
    RestResponse<?> mapException(IllegalStateException x) 
    {
        return responseForBadRequest(x.getMessage());
    }
    
    @ServerExceptionMapper
    RestResponse<?> mapException(IllegalArgumentException x) 
    {
        return responseForBadRequest(x.getMessage());
    }
}
