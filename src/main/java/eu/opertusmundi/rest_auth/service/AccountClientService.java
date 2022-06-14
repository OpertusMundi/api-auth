package eu.opertusmundi.rest_auth.service;

import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import eu.opertusmundi.rest_auth.domain.AccountClientEntity;
import eu.opertusmundi.rest_auth.model.AccountClientDto;
import eu.opertusmundi.rest_auth.model.ClientDto;
import eu.opertusmundi.rest_auth.repository.AccountClientRepository;
import io.smallrye.mutiny.Uni;

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
            return Uni.createFrom().nullItem();
        }
        
        return this.fetch(clientId);
    }
    
    public Uni<AccountClientDto> fetch(UUID clientId)
    {
        return accountClientRepository.fetch(clientId)
            .map(defaultEntityToDtoMapper)
            .onFailure().recoverWithNull();
    }
}
