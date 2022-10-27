package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountSubscriptionRepository;
import eu.opertusmundi.api_auth.domain.AccountSubscriptionEntity;
import eu.opertusmundi.api_auth.model.AccountSubscriptionDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class AccountSubscriptionService
{
    @Inject
    AccountSubscriptionRepository accountSubscriptionRepository;
    
    private final Function<AccountSubscriptionEntity, AccountSubscriptionDto> briefToDtoMapper =
        accountSubscriptionEntity -> accountSubscriptionEntity.toDto(false /* convertAccountToDto */);
        
    private final Function<AccountSubscriptionEntity, AccountSubscriptionDto> fullToDtoMapper =
        accountSubscriptionEntity -> accountSubscriptionEntity.toDto(true /* convertAccountToDto */);
        
    public Uni<AccountSubscriptionDto> findByKey(@NotBlank String keyAsString, boolean briefRepresentation)
    {
        UUID key = null;
        try {
            key = UUID.fromString(keyAsString);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return this.findByKey(key, briefRepresentation);
    }
    
    public Uni<AccountSubscriptionDto> findByKey(@NotNull UUID key, boolean briefRepresentation)
    {
        return accountSubscriptionRepository.findByKey(key, !briefRepresentation /*fetchProviderAndConsumer*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper);
    }
    
    public Uni<AccountSubscriptionDto> findByKey(@NotNull UUID key)
    {
        return this.findByKey(key, true);
    }
    
    public Uni<AccountSubscriptionDto> findByKey(@NotBlank String keyAsString)
    {
        return this.findByKey(keyAsString, true);
    }
    
    public Uni<AccountSubscriptionDto> findById(Integer id, boolean briefRepresentation)
    {
        return accountSubscriptionRepository.findById(id, !briefRepresentation /*fetchProviderAndConsumer*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper);
    }
}
