package eu.opertusmundi.rest_auth;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;

import eu.opertusmundi.rest_auth.model.ClientDto;
import eu.opertusmundi.rest_auth.service.AccountClientService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

@Path("/hello")
public class GreetingController
{
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class TokenInfo 
    {
        String name;
        
        Set<String> roles;
        
        @lombok.With
        ClientDto client;
    }
    
    @Inject
    SecurityIdentity securityIdentity;
    
    @Inject
    AccountClientService accountClientService;
    
    @Authenticated
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<? extends TokenInfo> tokenInfo() 
    {
        final Set<String> roles = securityIdentity.getRoles();
        final JsonWebToken jwt = (JsonWebToken) securityIdentity.getPrincipal();
        final String name = jwt.getName(); 
        
        final TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setName(name);
        tokenInfo.setRoles(roles);
        
        final String clientId = jwt.getClaim("clientId");
        if (clientId == null) {
            return Uni.createFrom().item(tokenInfo);
        }
        
        return accountClientService.fetch(clientId)
            .map(ClientDto.class::cast)
            .replaceIfNullWith(() -> new ClientDto(clientId))
            .map(tokenInfo::withClient);
    }
}
