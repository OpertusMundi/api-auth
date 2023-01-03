package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountRepository;
import eu.opertusmundi.api_auth.domain.AccountEntity;
import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Named("defaultAccountService")
public class DefaultAccountService implements AccountService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAccountService.class);
    
    @Inject
    AccountRepository accountRepository;
    
    private final Function<AccountEntity, AccountDto> briefToDtoMapper = accountEntity -> 
        accountEntity.toDto(false /*includeParent*/, false /*includeClients*/, false /*backLink*/);
    
    private final Function<AccountEntity, AccountDto> fullToDtoMapper = accountEntity -> 
        accountEntity.toDto(true /*includeParent*/, true /*includeClients*/, true /*backLink*/);  
        
    @Override
    public Uni<AccountDto> findByKey(@NotNull UUID key, boolean briefRepresentation)
    {
        return accountRepository.findByKey(key, !briefRepresentation /*fetchAssociatedClients*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper)
            .onFailure(NoResultException.class)
                .recoverWithItem(exception -> {
                    LOGGER.info("no account for key [" + key + "]", exception);
                    return null; // recover with null
                });
    }
    
    @Override
    public Uni<AccountDto> findById(int id, boolean briefRepresentation)
    {
        return accountRepository.findById(id, !briefRepresentation /*fetchAssociatedClients*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper)
            .onFailure(NoResultException.class)
                .recoverWithItem(exception -> {
                    LOGGER.info("no account for id [" + id + "]", exception);
                    return null; // recover with null
                });
    }
    
    @Override
    public Uni<AccountDto> findByEmail(@NotBlank String email, boolean briefRepresentation)
    {
        return accountRepository.findByEmail(email, !briefRepresentation /*fetchAssociatedClients*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper)
            .onFailure(NoResultException.class)
                .recoverWithItem(exception -> {
                    LOGGER.info("no account for email [" + email + "]", exception);
                    return null; // recover with null
                });
    }
}
