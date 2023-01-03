package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountClientRepository;
import eu.opertusmundi.api_auth.domain.AccountClientEntity;
import eu.opertusmundi.api_auth.model.AccountClientDto;

@ApplicationScoped
@Named("defaultAccountClientService")
public class DefaultAccountClientService implements AccountClientService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAccountClientService.class);
    
    @Inject
    AccountClientRepository accountClientRepository;

    private final Function<AccountClientEntity, AccountClientDto> briefToDtoMapper =
        accountClientEntity -> accountClientEntity.toDto(false /* convertAccountToDto */);
        
    private final Function<AccountClientEntity, AccountClientDto> fullToDtoMapper =
        accountClientEntity -> accountClientEntity.toDto(true /* convertAccountToDto */);
    
    @Override
    public Uni<AccountClientDto> findByKey(@NotNull UUID key, boolean briefRepresentation)
    {
        return accountClientRepository.findByKey(key, !briefRepresentation)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper)
            .onFailure(NoResultException.class)
                .recoverWithItem(exception -> {
                    LOGGER.info("no account client for key [" + key + "]", exception);
                    return null; // recover with null
                });
    }
}
