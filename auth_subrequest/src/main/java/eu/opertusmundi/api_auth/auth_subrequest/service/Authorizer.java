package eu.opertusmundi.api_auth.auth_subrequest.service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.opertusmundi.api_auth.auth_subrequest.model.Request;
import eu.opertusmundi.api_auth.auth_subrequest.model.exception.ConsumerNotAuthorizedForResourceException;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

public interface Authorizer <R extends Request>
{
    /**
     * Authorize a consumer trying to perform a request on a provider's resource 
     * 
     * @param consumerAccountClient The AccountClient DTO of a consumer
     * @param providerAccount The Account DTO of the provider who owns the target resource
     * @param requestId An identifier for the end-to-end request (generated at the ingress tier)
     * @param request The request targeting a protected resource
     * 
     * @return An empty {@code Uni} on success, otherwise a failed {@code Uni} carrying an exception. 
     * 
     * @throws ConsumerNotAuthorizedForResourceException when consumer is denied access to target resource
     * @throws IllegalStateException when a more generic error has occurred
     */
    public Uni<Void> authorize(
        @NotNull @Valid AccountClientDto consumerAccountClient, 
        @NotNull @Valid AccountDto providerAccount,
        @NotBlank String requestId, 
        @NotNull @Valid R request);
}
