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
    Uni<AccountSubscriptionDto> findByKey(@NotBlank String keyAsString, boolean briefRepresentation);

    Uni<AccountSubscriptionDto> findByKey(@NotNull UUID key, boolean briefRepresentation);

    Uni<AccountSubscriptionDto> findByKey(@NotNull UUID key);

    Uni<AccountSubscriptionDto> findByKey(@NotBlank String keyAsString);

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