package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import eu.opertusmundi.api_auth.auth_subrequest.model.exception.ConsumerNotAuthorizedForResourceException;
import eu.opertusmundi.api_auth.model.AccountDto;
import eu.opertusmundi.api_auth.model.AccountSubscriptionDto;
import io.smallrye.mutiny.Uni;

abstract class OwsAuthorizerBase
{
    static final Pattern LAYER_PATTERN = Pattern.compile(
        "(?<layerName>" +
            "_" +
            "(?<assetKey>[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})" + ")", 
        Pattern.CASE_INSENSITIVE);
    
    static String extractAssetKeyFromLayerName(String layerName)
    {
        final Matcher matcher = LAYER_PATTERN.matcher(layerName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("layer name is malformed: [" + layerName + "]");
        }
        return matcher.group("assetKey");
    }
    
    static List<String> extractAssetKeysFromLayerNames(List<String> layerNames)
    {
        if (layerNames.isEmpty())
            return Collections.emptyList();
        else if (layerNames.size() == 1) // optimize common case of single layer
            return Collections.singletonList(extractAssetKeyFromLayerName(layerNames.get(0)));
        
        return layerNames.stream().map(s -> extractAssetKeyFromLayerName(s))
            .collect(Collectors.toUnmodifiableList());
    }
    
    @Inject
    AssetResourceService assetResourceService;
    
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
