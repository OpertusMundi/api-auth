package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountRepository;
import eu.opertusmundi.api_auth.domain.AccountEntity;
import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class AccountService
{
    @Inject
    AccountRepository accountRepository;
    
    private final Function<AccountEntity, AccountDto> defaultEntityToDtoMapper = accountEntity -> 
        accountEntity.toDto(false /*includeParent*/, true /*includeClients*/, false /*backLink*/);
    
    public Uni<AccountDto> fetchByKey(UUID key)
    {
        return accountRepository.fetchByKey(key)
            .map(defaultEntityToDtoMapper);
    }
    
    public Uni<AccountDto> fetchById(int id)
    {
        return accountRepository.fetchById(id)
            .map(defaultEntityToDtoMapper);
    }
}
