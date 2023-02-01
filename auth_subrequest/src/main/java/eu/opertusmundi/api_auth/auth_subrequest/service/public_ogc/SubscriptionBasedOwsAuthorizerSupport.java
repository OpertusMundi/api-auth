package eu.opertusmundi.api_auth.auth_subrequest.service.public_ogc;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.mutiny.Uni;

import eu.opertusmundi.api_auth.auth_subrequest.model.exception.ConsumerNotAuthorizedForResourceException;
import eu.opertusmundi.api_auth.auth_subrequest.service.AccountSubscriptionService;
import eu.opertusmundi.api_auth.auth_subrequest.service.LayerNamingStrategy;
import eu.opertusmundi.api_auth.model.AccountDto;
import eu.opertusmundi.api_auth.model.AccountSubscriptionDto;


abstract class SubscriptionBasedOwsAuthorizerSupport
{
    @ConfigProperty(
        name = "eu.opertusmundi.api_auth.auth_subrequest.service.public-ogc.allow-only-if-client-has-subscription", 
        defaultValue = "true")
    boolean allowOnlyIfClientHasSubscription = true;
    
    @Inject
    LayerNamingStrategy layerNamingStrategy;
    
    @Inject
    @Named("defaultAccountSubscriptionService")
    AccountSubscriptionService accountSubscriptionService;
    
    /**
     * Check that consumer subscriptions cover all requested assets
     * 
     * @param consumerAccount
     * @param providerAccount
     * @param assetKeys keys for requested assets
     * @return
     */
    protected Uni<Void> checkAssetKeysFromSubscriptions(
        AccountDto consumerAccount, AccountDto providerAccount, List<String> assetKeys)
    {
        final int consumerAccountId = consumerAccount.getId();
        final int providerAccountId = providerAccount.getId();
        
        return accountSubscriptionService.findByConsumerAndProviderAndAssetKeys(consumerAccountId, providerAccountId, assetKeys)
            .invoke(subscriptions -> {
                final List<String> assetKeysFromSubscriptions = subscriptions.stream()
                    .map(AccountSubscriptionDto::getAssetKey).collect(Collectors.toList());
                for (final String assetKey: assetKeys) {
                    if (!assetKeysFromSubscriptions.contains(assetKey))
                        throw new ConsumerNotAuthorizedForResourceException(consumerAccount.getKey(), assetKey);
                }
            })
            .replaceWithVoid();
    }
    
    /**
     * Check that consumer subscriptions cover requested asset
     * 
     * @param consumerAccount
     * @param providerAccount
     * @param assetKey key for requested asset
     * @return
     */
    protected Uni<Void> checkAssetKeyFromSubscriptions(
        final AccountDto consumerAccount, final AccountDto providerAccount, final String assetKey)
    {
        final int consumerAccountId = consumerAccount.getId();
        final int providerAccountId = providerAccount.getId();
        
        return accountSubscriptionService.findByConsumerAndProviderAndAssetKey(consumerAccountId, providerAccountId, assetKey)
            .invoke(subscriptions -> {
                if (subscriptions.isEmpty())
                    throw new ConsumerNotAuthorizedForResourceException(consumerAccount.getKey(), assetKey);
            })
            .replaceWithVoid();
    }
}
