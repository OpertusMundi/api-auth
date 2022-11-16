package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.opertusmundi.api_auth.model.AccountClientDto;
import io.smallrye.mutiny.Uni;

public interface AccountClientService
{
    static final boolean USE_BRIEF_REPRESENTATION = false;
    
    /**
     * Return a DTO representing an account client
     * 
     * @param key The client key 
     * @param briefRepresentation If false, returns a representation with the referenced account.
     *   Otherwise, it returns a minimal representation carrying only the account id. 
     * @return
     */
    Uni<AccountClientDto> findByKey(@NotNull UUID key, boolean briefRepresentation);

    default Uni<AccountClientDto> findByKey(@NotNull UUID key)
    {
        return this.findByKey(key, USE_BRIEF_REPRESENTATION);
    }

    /**
     * @see {@link DefaultAccountClientService#findByKey(UUID, boolean)}
     * 
     * @param keyAsString
     * @param briefRepresentation
     * @return
     */
    default Uni<AccountClientDto> findByKey(@NotBlank String keyAsString, boolean briefRepresentation)
    {
        UUID key = null;
        try {
            key = UUID.fromString(keyAsString);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return this.findByKey(key, briefRepresentation);
    }

    default Uni<AccountClientDto> findByKey(@NotBlank String keyAsString)
    {
        return this.findByKey(keyAsString, USE_BRIEF_REPRESENTATION);
    }
}