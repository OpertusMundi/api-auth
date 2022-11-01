package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountRepository;
import eu.opertusmundi.api_auth.domain.AccountEntity;
import eu.opertusmundi.api_auth.model.AccountDto;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class AccountService
{
    @Inject
    AccountRepository accountRepository;
    
    private final Function<AccountEntity, AccountDto> defaultToDtoMapper = accountEntity -> 
        accountEntity.toDto(false /*includeParent*/, false /*includeClients*/, false /*backLink*/);
    
    
    public Uni<AccountDto> findByKey(@NotBlank String keyAsString)
    {
        UUID key = null;
        try {
            key = UUID.fromString(keyAsString);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return this.findByKey(key);
    }
        
    @CacheResult(cacheName = "accountService.findByKey")
    public Uni<AccountDto> findByKey(@NotNull UUID key)
    {
        return accountRepository.findByKey(key, false /*fetchAssociatedClients*/)
            .map(defaultToDtoMapper)
            .onFailure(NoResultException.class)
                .transform(ex -> new IllegalStateException("no account for key: [" + key + "]"));
    }
    
    @CacheResult(cacheName = "accountService.findById")
    public Uni<AccountDto> findById(int id)
    {
        return accountRepository.findById(id, false /*fetchAssociatedClients*/)
            .map(defaultToDtoMapper)
            .onFailure(NoResultException.class)
                .transform(ex -> new IllegalStateException("no account for id: [" + id + "]"));
    }
}
