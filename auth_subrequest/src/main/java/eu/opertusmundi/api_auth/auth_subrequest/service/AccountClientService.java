package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountClientRepository;
import eu.opertusmundi.api_auth.domain.AccountClientEntity;
import eu.opertusmundi.api_auth.model.AccountClientDto;

@ApplicationScoped
public class AccountClientService
{
    @Inject
    AccountClientRepository accountClientRepository;

    private final Function<AccountClientEntity, AccountClientDto> briefToDtoMapper =
        accountClientEntity -> accountClientEntity.toDto(false /* convertAccountToDto */);
        
    private final Function<AccountClientEntity, AccountClientDto> fullToDtoMapper =
        accountClientEntity -> accountClientEntity.toDto(true /* convertAccountToDto */);
    
    /**
     * Return a DTO representing an account client
     * 
     * @param key The client key 
     * @param briefRepresentation If false, returns a representation with the referenced account.
     *   Otherwise, it returns a minimal representation carrying only the account id. 
     * @return
     */
    public Uni<AccountClientDto> findByKey(@NotNull UUID key, boolean briefRepresentation)
    {
        return accountClientRepository.findByKey(key, !briefRepresentation)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper)
            .onFailure(NoResultException.class)
                .transform(ex -> new IllegalStateException("no account client for key: [" + key + "]", ex));
    }
    
    public Uni<AccountClientDto> findByKey(@NotNull UUID key)
    {
        return this.findByKey(key, false);
    }
    
    /**
     * @see {@link AccountClientService#findByKey(UUID, boolean)}
     * 
     * @param keyAsString
     * @param briefRepresentation
     * @return
     */
    public Uni<AccountClientDto> findByKey(@NotBlank String keyAsString, boolean briefRepresentation)
    {
        UUID key = null;
        try {
            key = UUID.fromString(keyAsString);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return this.findByKey(key, briefRepresentation);
    }
    
    public Uni<AccountClientDto> findByKey(@NotBlank String keyAsString)
    {
        return this.findByKey(keyAsString, false);
    }
}
