package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

public interface AccountService
{
    default Uni<AccountDto> findByKey(@NotBlank String keyAsString)
    {
        UUID key = null;
        try {
            key = UUID.fromString(keyAsString);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return this.findByKey(key);
    }

    Uni<AccountDto> findByKey(@NotNull UUID key);

    Uni<AccountDto> findById(int id);
}