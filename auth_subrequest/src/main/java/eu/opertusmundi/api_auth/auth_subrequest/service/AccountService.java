package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

public interface AccountService
{
    static final boolean USE_BRIEF_REPRESENTATION = true;
    
    Uni<AccountDto> findByKey(@NotNull UUID key, boolean briefRepresentation);
    
    Uni<AccountDto> findById(int id, boolean briefRepresentation);
    
    Uni<AccountDto> findByEmail(@NotBlank String email, boolean briefRepresentation);
    
    default Uni<AccountDto> findByKey(@NotBlank String keyAsString, boolean briefRepresentation)
    {
        UUID key = null;
        try {
            key = UUID.fromString(keyAsString);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return this.findByKey(key, briefRepresentation);
    }

    default Uni<AccountDto> findByKey(@NotBlank String keyAsString)
    {
        return this.findByKey(keyAsString, USE_BRIEF_REPRESENTATION);
    }
    
    default Uni<AccountDto> findByKey(@NotNull UUID key)
    {
        return this.findByKey(key, USE_BRIEF_REPRESENTATION);
    }
    
    default Uni<AccountDto> findById(int id)
    {
        return findById(id, USE_BRIEF_REPRESENTATION);
    }
    
    default Uni<AccountDto> findByEmail(@NotBlank String email)
    {
        return this.findByEmail(email, USE_BRIEF_REPRESENTATION);
    }
}