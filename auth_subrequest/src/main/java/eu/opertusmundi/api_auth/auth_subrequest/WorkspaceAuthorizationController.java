package eu.opertusmundi.api_auth.auth_subrequest;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
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

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.opertusmundi.api_auth.auth_subrequest.model.OwsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.TmsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WfsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmtsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.event.AuthorizationGrantedEvent;
import eu.opertusmundi.api_auth.auth_subrequest.model.event.AuthorizationGrantedForWorkspaceResourceEvent;
import eu.opertusmundi.api_auth.auth_subrequest.model.exception.AccountClientIsRevokedException;
import eu.opertusmundi.api_auth.auth_subrequest.model.exception.ConsumerNotAuthorizedForResourceException;
import eu.opertusmundi.api_auth.auth_subrequest.model.exception.ConsumerNotAuthorizedForWorkspaceException;
import eu.opertusmundi.api_auth.auth_subrequest.service.AccountClientService;
import eu.opertusmundi.api_auth.auth_subrequest.service.AccountService;
import eu.opertusmundi.api_auth.auth_subrequest.service.Authorizer;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;
import eu.opertusmundi.api_auth.model.WorkspaceInfo;
import eu.opertusmundi.api_auth.model.WorkspaceType;

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
        return WorkspaceInfo.of(type, providerAccountKey);
    }
    
    @Context
    SecurityIdentity securityIdentity;

    @Context
    HttpHeaders httpHeaders;
    
    @Context
    UriInfo uriInfo;
    
    @Inject
    @Named("cachingAccountClientService")
    AccountClientService accountClientService;
    
    @Inject
    @Named("cachingAccountService")
    AccountService accountService;
    
    @Inject
    @Named("public_ogc.WmsAuthorizer")
    Authorizer<WmsRequest> wmsAuthorizerForPublicWorkspace;
    
    @Inject
    @Named("public_ogc.WmtsAuthorizer")
    Authorizer<WmtsRequest> wmtsAuthorizerForPublicWorkspace;
    
    @Inject
    @Named("public_ogc.WfsAuthorizer")
    Authorizer<WfsRequest> wfsAuthorizerForPublicWorkspace;
    
    @Inject
    @Named("public_ogc.TmsAuthorizer")
    Authorizer<TmsRequest> tmsAuthorizerForPublicWorkspace;
    
    @Inject
    @Named("private_ogc.OwsAuthorizer")
    Authorizer<OwsRequest> owsAuthorizerForPrivateWorkspace;
    
    @Inject
    Event<AuthorizationGrantedForWorkspaceResourceEvent> successEvent;
    
    @GET
    @Path("/wms")
    public Uni<RestResponse<?>> authorizeWms(
        @HeaderParam(ORIG_METHOD_HEADER_NAME) final String origRequestMethod,
        @HeaderParam(AUTH_REQUEST_REDIRECT_HEADER_NAME) final URI authRequestRedirect)
    {
        if (!HttpMethod.GET.name().equals(origRequestMethod)) {
            return Uni.createFrom().item(responseForBadRequest(
                "only GET (KVP-style) is allowed for WMS requests [origRequestMethod=" + origRequestMethod + "]"));
        }
               
        final WorkspaceInfo workspaceInfo = workspaceInfo();
        final WmsRequest request = WmsRequest.fromMap(parseQueryStringToMap(authRequestRedirect));
        switch (workspaceInfo.getType()) {
        case PRIVATE:
            return authorize1(request, workspaceInfo, authRequestRedirect, owsAuthorizerForPrivateWorkspace);
        case PUBLIC:
            return authorize1(request, workspaceInfo, authRequestRedirect, wmsAuthorizerForPublicWorkspace);
        default:
            throw new IllegalStateException("unknown workspace type: [" + workspaceInfo.getType() + "]");
        }
    }
    
    @GET
    @Path("/gwc/service/wmts")
    public Uni<RestResponse<?>> authorizeWmts(
        @HeaderParam(ORIG_METHOD_HEADER_NAME) final String origRequestMethod,
        @HeaderParam(AUTH_REQUEST_REDIRECT_HEADER_NAME) final URI authRequestRedirect)
    {
        if (!HttpMethod.GET.name().equals(origRequestMethod)) {
            return Uni.createFrom().item(responseForBadRequest(
                "only GET (KVP-style) is allowed for WMTS requests [origRequestMethod=" + origRequestMethod + "]"));
        }
        
        final WorkspaceInfo workspaceInfo = workspaceInfo();
        final WmtsRequest request = WmtsRequest.fromMap(parseQueryStringToMap(authRequestRedirect));
        switch (workspaceInfo.getType()) {
        case PRIVATE:
            return authorize1(request, workspaceInfo, authRequestRedirect, owsAuthorizerForPrivateWorkspace);
        case PUBLIC:
            return authorize1(request, workspaceInfo, authRequestRedirect, wmtsAuthorizerForPublicWorkspace);
        default:
            throw new IllegalStateException("unknown workspace type: [" + workspaceInfo.getType() + "]");
        }
    }
    
    // See also: https://www.geowebcache.org/docs/current/services/tms.html
    @GET
    @Path("/gwc/service/tms/{serviceVersion:[1]\\.[0]\\.[0]}/{layer}/{z:[0-9]+}/{x:[0-9]+}/{fileName:[0-9]+\\.(png|jpeg|pbf)}")
    public Uni<RestResponse<?>> authorizeTms(
        @HeaderParam(ORIG_METHOD_HEADER_NAME) final String origRequestMethod,
        @HeaderParam(AUTH_REQUEST_REDIRECT_HEADER_NAME) final URI authRequestRedirect,
        @PathParam("serviceVersion") String serviceVersion, 
        @PathParam("layer") String layer,
        @PathParam("z") int z, 
        @PathParam("x") int x,
        @PathParam("fileName") String fileName)
    {
        if (!HttpMethod.GET.name().equals(origRequestMethod)) {
            return Uni.createFrom().item(responseForBadRequest(
                "only GET is allowed for TMS requests [origRequestMethod=" + origRequestMethod + "]"));
        }
        
        final String[] layerParts = StringUtils.split(layer, '@');
        Validate.isTrue(layerParts.length == 3, 
            "layer path component is expected as {layerName}@{gridsetId}@{formatExtension}");
        final String layerName = layerParts[0];
        Validate.notBlank(layerName, "layerName must not be blank");
        final String gridsetId = layerParts[1];
        Validate.notBlank(gridsetId, "gridsetId must not be blank");
        final String formatExtension = layerParts[2];
        Validate.notBlank(formatExtension, "formatExtension must not be blank");
        
        final String[] fileNameParts = StringUtils.split(fileName, '.');
        Validate.isTrue(fileNameParts.length == 2, 
            "fileName component is expected as {y}.{formatExtension}");
        final int y = Integer.parseInt(fileNameParts[0]);
        Validate.isTrue(formatExtension.equalsIgnoreCase(fileNameParts[1]), 
            "fileName extension must be same as format extension specified at layer path component");
        
        final WorkspaceInfo workspaceInfo = workspaceInfo();
        final TmsRequest request = TmsRequest.builder()
            .serviceVersion(TmsRequest.ServiceVersion.fromString(serviceVersion))
            .layerName(layerName)
            .gridsetId(gridsetId)
            .outputFormat(TmsRequest.OutputFormat.fromExtension(formatExtension))
            .z(z).x(x).y(y)
            .build();
        
        switch (workspaceInfo.getType()) {
        case PRIVATE:
            return authorize1(request, workspaceInfo, authRequestRedirect, owsAuthorizerForPrivateWorkspace);
        case PUBLIC:
            return authorize1(request, workspaceInfo, authRequestRedirect, tmsAuthorizerForPublicWorkspace);
        default:
            throw new IllegalStateException("unknown workspace type: [" + workspaceInfo.getType() + "]");
        }
    }
    
    @GET
    @Path("/wfs")
    public Uni<RestResponse<?>> authorizeWfs(
        @HeaderParam(ORIG_METHOD_HEADER_NAME) final String origRequestMethod,
        @HeaderParam(AUTH_REQUEST_REDIRECT_HEADER_NAME) final URI authRequestRedirect)
    {
        if (!HttpMethod.GET.name().equals(origRequestMethod)) {
            return Uni.createFrom().item(responseForBadRequest(
                "only GET (KVP-style) is allowed for WFS requests [origRequestMethod=" + origRequestMethod + "]"));
        }
        
        final WorkspaceInfo workspaceInfo = workspaceInfo();
        final WfsRequest request = WfsRequest.fromMap(parseQueryStringToMap(authRequestRedirect));
        switch (workspaceInfo.getType()) {
        case PRIVATE:
            return authorize1(request, workspaceInfo, authRequestRedirect, owsAuthorizerForPrivateWorkspace);
        case PUBLIC:
            return authorize1(request, workspaceInfo, authRequestRedirect, wfsAuthorizerForPublicWorkspace);
        default:
            throw new IllegalStateException("unknown workspace type: [" + workspaceInfo.getType() + "]");
        }
    }
    
    private <R extends OwsRequest> Uni<RestResponse<?>> authorize1(
        R request, WorkspaceInfo workspaceInfo, URI authRequestRedirect, Authorizer<? super R> authorizer)
    {
        final String requestId = this.requestId();
        
        final Uni<AccountClientDto> consumerAccountClientUni = this.consumerAccountClient();
        final Uni<AccountDto> providerAccountUni = this.providerAccount(workspaceInfo);
        
        final Uni<Void> authorizationUni = consumerAccountClientUni.chain(consumerAccountClient -> 
            providerAccountUni.chain(providerAccount -> 
                authorizer.authorize(consumerAccountClient, providerAccount, requestId, request)
                    // Fire event on success
                    .invoke(() -> successEvent.fireAsync(AuthorizationGrantedForWorkspaceResourceEvent.builder()
                        .when(ZonedDateTime.now())
                        .requestId(requestId)
                        .request(request)
                        .authRequestRedirect(authRequestRedirect)
                        .originalUrl(originalUrl())
                        .workspaceInfo(workspaceInfo)
                        .consumerAccountClient(consumerAccountClient)
                        .build()))
            ));
        
        return authorizationUni.onItemOrFailure()
            .transform((nullItem, exception) -> {
                if (exception == null) {
                    // no exception received: success
                    return responseForSuccess();
                } else if (exception instanceof AccountClientIsRevokedException
                        || exception instanceof ConsumerNotAuthorizedForResourceException
                        || exception instanceof ConsumerNotAuthorizedForWorkspaceException) {
                    return responseForNotAuthorized(exception);
                } else {
                    LOGGER.info("Unexpected failure during authorization of client", exception);
                    return responseForBadRequest(exception);
                }
            });
    }
    
    /**
     * Map {@code securityIdentity} to a consumer account client
     */
    private Uni<AccountClientDto> consumerAccountClient()
    {
        final JsonWebToken jwt = (JsonWebToken) securityIdentity.getPrincipal();
        
        Uni<AccountClientDto> consumerAccountClientUni = null;
        
        final String clientKey = jwt.getClaim("clientId");
        if (clientKey != null) {
            // assuming an access token from a `client_credentials` grant
            Validate.validState(!StringUtils.isBlank(clientKey), 
                "claim [clientId] cannot be blank");
            Validate.validState(clientKey.equals(jwt.getClaim(Claims.azp)), 
                "claim [clientId] is different from claim [azp]");
            consumerAccountClientUni = accountClientService.findByKey(clientKey, false /*brief*/)
                .onItem().ifNull().failWith(
                    () -> new NoSuchElementException("no client for key [key=" + clientKey + "]"));
        } else {
            // assuming an access token from an `authorization_code` grant
            final String email = jwt.getClaim(Claims.email);
            Validate.validState(!StringUtils.isBlank(email), 
                "claim [email] must not be blank (since claim [clientId] is missing)");
            consumerAccountClientUni = accountService.findByEmail(email, false /*brief*/)
                .onItem().ifNull().failWith(
                    () -> new NoSuchElementException("no account for email [email=" + email + "]"))
                .map(accountDto -> {
                    return accountDto.getClients().stream()
                        // select the default client for the account (client key is equal to account key)
                        .filter(c -> c.getKeyAsUuid().equals(accountDto.getKey()))
                        .findFirst().orElse(null);
                })
                .onItem().ifNull().failWith(
                    () -> new NoSuchElementException("no client associated with account [email=" + email + "]"));
        }
        
        return consumerAccountClientUni
            .invoke(accountClientDto -> {
                if (accountClientDto.getRevoked() != null) {
                    throw new AccountClientIsRevokedException(accountClientDto.getKey());
                }
            });
    }
    
    /**
     * Map workspace information to a provider account
     * 
     * @param workspaceInfo
     */
    private Uni<AccountDto> providerAccount(WorkspaceInfo workspaceInfo)
    {
        final UUID providerAccountKey = workspaceInfo.getProviderAccountKey();
        return accountService.findByKey(providerAccountKey)
            .onItem().ifNull().failWith(
                () -> new NoSuchElementException("no account with key [key=" + providerAccountKey + "]"));
    }
    
    private WorkspaceInfo workspaceInfo()
    {
        final MultivaluedMap<String, String> pathParameters = uriInfo.getPathParameters();
        return workspaceInfoFromString(pathParameters.getFirst("workspace"));
    }
        
    private String requestId()
    {
        return this.httpHeaders.getHeaderString(REQUEST_ID_HEADER_NAME);
    }
    
    private URL originalUrl()
    {
        try {
            return new URL(this.httpHeaders.getHeaderString(ORIG_URL_HEADER_NAME));
        } catch (MalformedURLException ex) {
            throw new IllegalStateException(ex);
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
