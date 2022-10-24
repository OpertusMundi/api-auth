package eu.opertusmundi.api_auth.auth_subrequest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

import static eu.opertusmundi.api_auth.auth_subrequest.ExtraHttpHeaders.*;

import java.net.URI;

@Authenticated
@Path("/authorize/httpbin")
public class HttpbinAuthorizationController
{
    @Context
    SecurityIdentity securityIdentity;
    
    @Context
    HttpHeaders httpHeaders;
    
    @Context
    UriInfo uriInfo;
    
    @GET
    @Path("/{subpath:.+}")
    //@Path("/get")
    public Uni<RestResponse<Object>> authorize(
        @HeaderParam(AUTH_REQUEST_REDIRECT_HEADER_NAME) final URI authRequestRedirect)
    {
        return Uni.createFrom().item(responseForSuccess());
    }
    
    private RestResponse<Object> responseForSuccess()
    {
        return RestResponse.ResponseBuilder.noContent()
            .header(REMOTE_USER_HEADER_NAME, securityIdentity.getPrincipal().getName())
            .build();
    }
}
