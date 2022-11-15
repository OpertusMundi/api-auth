package eu.opertusmundi.api_auth.auth_subrequest;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import eu.opertusmundi.api_auth.auth_subrequest.service.AccountClientService;
import eu.opertusmundi.api_auth.model.ClientDto;

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
        
        final String clientKey = jwt.getClaim("clientId");
        if (clientKey == null) {
            return Uni.createFrom().item(tokenInfo);
        }
        
        return accountClientService.findByKey(UUID.fromString(clientKey))
            .map(ClientDto.class::cast)
            .replaceIfNullWith(() -> new ClientDto(clientKey))
            .map(tokenInfo::withClient);
    }
}
