package eu.opertusmundi.api_auth.auth_subrequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
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

import eu.opertusmundi.api_auth.auth_subrequest.model.BaseRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.ConsumerNotAuthorizedForResourceException;
import eu.opertusmundi.api_auth.auth_subrequest.model.ConsumerNotAuthorizedForWorkspace;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WorkspaceInfo;
import eu.opertusmundi.api_auth.auth_subrequest.model.WorkspaceType;
import eu.opertusmundi.api_auth.auth_subrequest.service.AccountClientService;
import eu.opertusmundi.api_auth.auth_subrequest.service.AccountService;
import eu.opertusmundi.api_auth.auth_subrequest.service.Authorizer;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.opertusmundi.api_auth.auth_subrequest.util.QueryStringUtil.*;
import static eu.opertusmundi.api_auth.auth_subrequest.ExtraHttpHeaders.*;

/**
 * Authorize requests for OGC services in the workspace of a provider user.
 */
@Authenticated
@Path("/authorize/{workspace:([a-z][-a-z0-9]*)?_([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})}")
public class WorkspaceAuthorizationController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceAuthorizationController.class);
    
    static final Pattern WORKSPACE_PATTERN = Pattern.compile(
        "(?<workspace>" +
            "(?<workspacePrefix>[a-z][-a-z0-9]*)?" + "_" +
            "(?<providerAccountKey>[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})" + ")", 
        Pattern.CASE_INSENSITIVE);
    
    private static WorkspaceInfo workspaceInfoFromString(String workspaceString)
    {
        final Matcher workspaceMatcher = WORKSPACE_PATTERN.matcher(workspaceString);
        if (!workspaceMatcher.matches()) {
            throw new IllegalArgumentException("workspace is malformed: [" + workspaceString + "]");
        }
        final WorkspaceType type = WorkspaceType.fromPrefix(workspaceMatcher.group("workspacePrefix"));
        final String providerAccountKey = workspaceMatcher.group("providerAccountKey");
        return new WorkspaceInfo(type, providerAccountKey);
    }
    
    @Context
    SecurityIdentity securityIdentity;

    @Context
    HttpHeaders httpHeaders;
    
    @Context
    UriInfo uriInfo;
    
    @Inject
    AccountClientService accountClientService;
    
    @Inject
    AccountService accountService;
    
    @Inject
    @Named("publicWmsEndpointAuthorizer")
    Authorizer<WmsRequest> publicWmsEndpointAuthorizer;
    
    @GET
    @Path("/wms")
    public Uni<RestResponse<?>> authorizeForWms(
        @HeaderParam(AUTH_REQUEST_REDIRECT_HEADER_NAME) final URI authRequestRedirect)
    {
        final Supplier<WmsRequest> requestSupplier = 
            () -> WmsRequest.fromMap(parseQueryStringToMap(authRequestRedirect));
        return authorizeForRequest(requestSupplier, publicWmsEndpointAuthorizer);
    }

    private <R extends BaseRequest> Uni<RestResponse<?>> authorizeForRequest(
        Supplier<? extends R> requestSupplier, Authorizer<R> publicEndpointAuthorizer)
    {
        final String clientKey = clientKeyFromContext();
        final WorkspaceInfo workspaceInfo = workspaceInfoFromContext();
        final String requestId = requestIdFromContext();
        
        final Uni<AccountClientDto> consumerAccountClientUni = accountClientService.findByKey(clientKey, false /*brief*/);
        final Uni<AccountDto> providerAccountUni = accountService.findByKey(workspaceInfo.getProviderAccountKey());
        
        final Uni<Void> authorizationUni = consumerAccountClientUni.chain(consumerAccountClient -> {
           return providerAccountUni.chain(providerAccount -> {
               final var request = requestSupplier.get(); // parse request
               final var workspaceType = workspaceInfo.getType();
               final int consumerAccountId = consumerAccountClient.getAccountId();
               final int providerAccountId = providerAccount.getId();
               
               if (consumerAccountId == providerAccountId) {
                   // consumer is same with provider (success)
                   return Uni.createFrom().nullItem();
               } else if (workspaceType == WorkspaceType.PRIVATE) {
                   // consumer is accessing a private workspace 
                   final AccountDto consumerAccount = consumerAccountClient.getAccount();
                   final int consumerParentAccountId = Optional.of(consumerAccount)
                       .map(AccountDto::getParentId).orElse(-1);
                   return (consumerParentAccountId == providerAccountId)? Uni.createFrom().nullItem() /*success*/: 
                       Uni.createFrom().failure(new ConsumerNotAuthorizedForWorkspace(consumerAccount.getKey(), workspaceInfo)); 
               } else if (workspaceType == WorkspaceType.PUBLIC) {
                   // consumer is accessing a public workspace 
                   return publicEndpointAuthorizer.authorize(consumerAccountClient, providerAccount, requestId, request);
               } else {
                   return Uni.createFrom().failure(
                       new IllegalStateException("unknown workspace type: [" + workspaceType + "]"));
               }
           }); 
        });
        
        return authorizationUni.onItemOrFailure()
            .transform((item, ex) -> transformFailureFromAuthorization(
                ex, clientKey, workspaceInfo.getProviderAccountKey(), requestId));
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
        return workspaceInfoFromString(pathParameters.getFirst("workspace"));
    }
        
    private String requestIdFromContext()
    {
        return this.httpHeaders.getHeaderString(REQUEST_ID_HEADER_NAME);
    }
    
    private RestResponse<?> transformFailureFromAuthorization(
        Throwable x, String clientKey, String providerAccountKey, String requestId)
    {
        if (x == null) {
            // no exception received: success
            return responseForSuccess();
        } else if (x instanceof ConsumerNotAuthorizedForResourceException 
                || x instanceof ConsumerNotAuthorizedForWorkspace) {
            return responseForNotAuthorized(x);
        } else {
            final String message = "Unexpected failure during authorization of client [" + clientKey + "]";
            LOGGER.info(message, x);
            return responseForBadRequest(x);
        }
    }
    
    private RestResponse<Object> responseForBadRequest(Throwable ex)
    {
        return responseForBadRequest(Objects.requireNonNull(ex).getMessage());
    }
    
    private RestResponse<Object> responseForBadRequest(String message)
    {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST)
            .header(ERROR_MESSAGE_HEADER_NAME, message.lines().findFirst().get())
            .build();
    }
    
    private RestResponse<Object> responseForNotAuthorized(Throwable ex)
    {
        return responseForNotAuthorized(Objects.requireNonNull(ex).getMessage());
    }
    
    private RestResponse<Object> responseForNotAuthorized(String message)
    {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.FORBIDDEN)
            .header(ERROR_MESSAGE_HEADER_NAME, message.lines().findFirst().get())
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
