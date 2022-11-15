package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

public interface AccountService
{
    Uni<AccountDto> findByKey(@NotBlank String keyAsString);

    Uni<AccountDto> findByKey(@NotNull UUID key);

    Uni<AccountDto> findById(int id);
}