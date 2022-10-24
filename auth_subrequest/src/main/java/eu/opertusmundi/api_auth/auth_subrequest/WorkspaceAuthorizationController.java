package eu.opertusmundi.api_auth.auth_subrequest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
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
    
    private static final Pattern WORKSPACE_PATTERN = Pattern.compile("(?<workspace>" +
            "(?<workspacePrefix>[a-z][-a-z0-9]*)?" + "_" +
            "(?<providerAccountKey>[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})" + ")", 
        Pattern.CASE_INSENSITIVE);
    
    private static class WorkspaceInfo
    {
        String prefix;
        
        String providerAccountKey;
    }

    @Inject
    Authorizer<WmsRequest> wmsAuthorizer;
    
    @Inject
    SecurityIdentity securityIdentity;
 
    @GET
    @Path("/wms")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<Object>> authorizeForWms(
        @PathParam("workspace") final String workspace,
        @HeaderParam(REQUEST_ID_HEADER_NAME) final String requestId,
        @HeaderParam(AUTH_REQUEST_REDIRECT_HEADER_NAME) final URI authRequestRedirect)
    {
        final String clientKey = clientKeyFromJwt();
        final WorkspaceInfo workspaceInfo = workspaceInfoFromString(workspace);

        try {
            wmsAuthorizer.authorize(clientKey, workspaceInfo.providerAccountKey, 
                WmsRequest.fromMap(parseQueryStringToMap(authRequestRedirect)));
        } catch (ConsumerNotAuthorizedException x) {
            return Uni.createFrom().item(responseForForbidden(x.getMessage()));
        }
        
        return Uni.createFrom().item(responseForSuccess()); 
    }
    
    private String clientKeyFromJwt()
    {
        final JsonWebToken jwt = (JsonWebToken) securityIdentity.getPrincipal();
        
        final String clientKey = jwt.getClaim("clientId");
        Validate.validState(!StringUtils.isBlank(clientKey), 
            "claim [clientId] is missing from JWT token");
        Validate.validState(clientKey.equals(jwt.getClaim(Claims.azp)), 
            "claim [clientId] is different from claim [azp]");
        
        return clientKey;
    }
    
    private WorkspaceInfo workspaceInfoFromString(String workspace)
    {
        final Matcher workspaceMatcher = WORKSPACE_PATTERN.matcher(workspace);
        if (!workspaceMatcher.matches()) {
            throw new IllegalStateException("workspace is malformed: [" + workspace + "]");
        } 
        final WorkspaceInfo workspaceInfo = new WorkspaceInfo();
        workspaceInfo.prefix = workspaceMatcher.group("workspacePrefix");
        workspaceInfo.providerAccountKey = workspaceMatcher.group("providerAccountKey");
        return workspaceInfo;
    }
    
    private RestResponse<Object> responseForBadRequest(String message)
    {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST)
            .header(ERROR_MESSAGE_HEADER_NAME, message)
            .build();
    }
    
    private RestResponse<Object> responseForForbidden(String message)
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
