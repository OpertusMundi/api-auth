package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import eu.opertusmundi.api_auth.model.AccountSubscriptionDto;
import io.smallrye.mutiny.Uni;

public interface AccountSubscriptionService
{
    static final boolean USE_BRIEF_REPRESENTATION = true;
    
    Uni<AccountSubscriptionDto> findByKey(@NotNull UUID key, boolean briefRepresentation);
    
    default Uni<AccountSubscriptionDto> findByKey(@NotBlank String keyAsString, boolean briefRepresentation)
    {
        UUID key = null;
        try {
            key = UUID.fromString(keyAsString);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return this.findByKey(key, briefRepresentation);
    }

    default Uni<AccountSubscriptionDto> findByKey(@NotNull UUID key)
    {
        return this.findByKey(key, USE_BRIEF_REPRESENTATION);
    }

    default Uni<AccountSubscriptionDto> findByKey(@NotBlank String keyAsString)
    {
        return this.findByKey(keyAsString, USE_BRIEF_REPRESENTATION);
    }

    Uni<AccountSubscriptionDto> findById(@Positive int id, boolean briefRepresentation);

    Uni<List<AccountSubscriptionDto>> findByAssetKey(@NotBlank String assetKey);

    Uni<List<AccountSubscriptionDto>> findByAssetKeys(@NotEmpty List<String> assetKeys);

    Uni<List<AccountSubscriptionDto>> findByAsset(@NotBlank String assetPid);

    Uni<List<AccountSubscriptionDto>> findByConsumer(@Positive int consumerAccountId);

    Uni<List<AccountSubscriptionDto>> findByConsumerKey(@NotNull UUID consumerAccountKey);

    Uni<List<AccountSubscriptionDto>> findByProvider(@Positive int providerAccountId);

    Uni<List<AccountSubscriptionDto>> findByProviderKey(@NotNull UUID providerAccountKey);

    Uni<List<AccountSubscriptionDto>> findByConsumerAndAssetKey(
        @Positive int consumerAccountId, @NotBlank String assetKey);

    Uni<List<AccountSubscriptionDto>> findByConsumerAndProviderAndAssetKey(
        @Positive int consumerAccountId, @Positive int providerAccountId, @NotBlank String assetKey);

    Uni<List<AccountSubscriptionDto>> findByConsumerAndAssetKeys(
        @Positive int consumerAccountId, @NotEmpty List<String> assetKeys);

    Uni<List<AccountSubscriptionDto>> findByConsumerAndProviderAndAssetKeys(
        @Positive int consumerAccountId, @Positive int providerAccountId, @NotEmpty List<String> assetKeys);

    Uni<List<AccountSubscriptionDto>> findByConsumerAndAsset(
        @Positive int consumerAccountId, @NotBlank String assetPid);

    Uni<List<AccountSubscriptionDto>> findByConsumerAndProvider(
        @Positive int consumerAccountId, @Positive int providerAccountId);
}