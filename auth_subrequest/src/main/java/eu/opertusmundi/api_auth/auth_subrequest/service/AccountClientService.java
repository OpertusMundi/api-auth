package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountClientRepository;
import eu.opertusmundi.api_auth.domain.AccountClientEntity;
import eu.opertusmundi.api_auth.model.AccountClientDto;

@ApplicationScoped
public class AccountClientService
{
    @Inject
    AccountClientRepository accountClientRepository;

    private final Function<AccountClientEntity, AccountClientDto> defaultEntityToDtoMapper =
        accountClientEntity -> accountClientEntity.toDto(true /* convertAccountToDto */);
    
    public Uni<AccountClientDto> fetch(String clientIdAsString)
    {
        UUID clientId = null;
        try {
            clientId = UUID.fromString(clientIdAsString);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        
        return this.fetch(clientId);
    }
    
    public Uni<AccountClientDto> fetch(UUID clientId)
    {
        return accountClientRepository.fetch(clientId)
            .map(defaultEntityToDtoMapper);
    }
}
