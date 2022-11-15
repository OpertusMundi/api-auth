package eu.opertusmundi.api_auth.auth_subrequest;

import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.smallrye.mutiny.Uni;
import eu.opertusmundi.api_auth.auth_subrequest.service.AccountService;
import eu.opertusmundi.api_auth.model.AccountDto;

@RolesAllowed({"account-viewer"})
@Path("/accounts")
public class AccountController
{
    @Inject
    AccountService accountService;
    
    @GET
    @Path("/by-key/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<AccountDto> findAccountByKey(@PathParam("key") String keyAsString) 
    {
        return accountService.findByKey(keyAsString)
            .onFailure().recoverWithNull();
    }
    
    @GET
    @Path("/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<AccountDto> findAccountByKey(@PathParam("id") Integer id) 
    {
        return accountService.findById(id)
            .onFailure().recoverWithNull();
    }
}
