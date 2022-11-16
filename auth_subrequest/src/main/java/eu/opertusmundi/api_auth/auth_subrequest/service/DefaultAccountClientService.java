package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;

import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountClientRepository;
import eu.opertusmundi.api_auth.domain.AccountClientEntity;
import eu.opertusmundi.api_auth.model.AccountClientDto;

@ApplicationScoped
@Named("defaultAccountClientService")
public class DefaultAccountClientService implements AccountClientService
{
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
                .transform(ex -> new IllegalStateException("no account client for key: [" + key + "]", ex));
    }
}
