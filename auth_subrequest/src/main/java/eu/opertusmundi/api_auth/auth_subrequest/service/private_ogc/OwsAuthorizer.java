package eu.opertusmundi.api_auth.auth_subrequest.service.private_ogc;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.service.Authorizer;
import eu.opertusmundi.api_auth.auth_subrequest.model.OwsRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WorkspaceInfo;
import eu.opertusmundi.api_auth.auth_subrequest.model.exception.ConsumerNotAuthorizedForWorkspaceException;
import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;

@ApplicationScoped
@Named("private_ogc.OwsAuthorizer")
public class OwsAuthorizer implements Authorizer<OwsRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OwsAuthorizer.class);
    
    @Override
    public Uni<Void> authorize(
        @NotNull @Valid AccountClientDto consumerAccountClient,
        @NotNull @Valid AccountDto providerAccount, 
        @NotBlank String requestId, 
        @NotNull @Valid OwsRequest request)
    {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("authorize(): [consumerAccountClient={} providerAccount={} requestId={} request={}]", 
                consumerAccountClient, providerAccount, requestId, request);
        }
        
        final AccountDto consumerAccount = consumerAccountClient.getAccount();
        
        final UUID providerAccountKey = providerAccount.getKey();
        final UUID consumerAccountKey = consumerAccount.getKey();
        
        final int providerAccountId = providerAccount.getId();
        final int consumerAccountId = consumerAccountClient.getAccountId();
        final int consumerParentAccountId = Optional.of(consumerAccount)
            .map(AccountDto::getParentId).orElse(-1);
        
        if (consumerAccountId == providerAccountId /* consumer is same with provider */
                || consumerParentAccountId == providerAccountId) /* consumer is under provider's organization */ 
        {
            return Uni.createFrom().nullItem(); // success
        } else {
            return Uni.createFrom().failure(
                new ConsumerNotAuthorizedForWorkspaceException(
                    consumerAccountKey, WorkspaceInfo.forPrivateWorkspace(providerAccountKey)));
        }
    }
}
