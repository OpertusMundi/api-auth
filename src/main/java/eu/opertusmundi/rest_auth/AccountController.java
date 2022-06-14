package eu.opertusmundi.rest_auth;

import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.opertusmundi.rest_auth.model.AccountDto;
import eu.opertusmundi.rest_auth.service.AccountService;
import io.smallrye.mutiny.Uni;

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
        return accountService.fetchByKey(UUID.fromString(keyAsString));
    }
    
    @GET
    @Path("/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<AccountDto> findAccountByKey(@PathParam("id") Integer id) 
    {
        return accountService.fetchById(id);
    }
}
