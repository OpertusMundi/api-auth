package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.opertusmundi.api_auth.model.AccountClientDto;
import io.smallrye.mutiny.Uni;

public interface AccountClientService
{
    /**
     * Return a DTO representing an account client
     * 
     * @param key The client key 
     * @param briefRepresentation If false, returns a representation with the referenced account.
     *   Otherwise, it returns a minimal representation carrying only the account id. 
     * @return
     */
    Uni<AccountClientDto> findByKey(@NotNull UUID key, boolean briefRepresentation);

    Uni<AccountClientDto> findByKey(@NotNull UUID key);

    /**
     * @see {@link DefaultAccountClientService#findByKey(UUID, boolean)}
     * 
     * @param keyAsString
     * @param briefRepresentation
     * @return
     */
    Uni<AccountClientDto> findByKey(@NotBlank String keyAsString, boolean briefRepresentation);

    Uni<AccountClientDto> findByKey(@NotBlank String keyAsString);
}