package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountSubscriptionRepository;
import eu.opertusmundi.api_auth.domain.AccountSubscriptionEntity;
import eu.opertusmundi.api_auth.model.AccountSubscriptionDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class AccountSubscriptionService
{
    @Inject
    AccountSubscriptionRepository accountSubscriptionRepository;
    
    private final Function<AccountSubscriptionEntity, AccountSubscriptionDto> briefToDtoMapper =
        accountSubscriptionEntity -> accountSubscriptionEntity.toDto(false /* convertAccountToDto */);
        
    private final Function<AccountSubscriptionEntity, AccountSubscriptionDto> fullToDtoMapper =
        accountSubscriptionEntity -> accountSubscriptionEntity.toDto(true /* convertAccountToDto */);
       
    private final Function<AccountSubscriptionEntity, AccountSubscriptionDto> defaultToDtoMapper = briefToDtoMapper;
    
    private final Function<List<AccountSubscriptionEntity>, List<AccountSubscriptionDto>> defaultResultsMapper =
        results -> results.stream().map(defaultToDtoMapper).collect(Collectors.toUnmodifiableList());
        
    public Uni<AccountSubscriptionDto> findByKey(@NotBlank String keyAsString, boolean briefRepresentation)
    {
        UUID key = null;
        try {
            key = UUID.fromString(keyAsString);
        } catch (IllegalArgumentException ex) {
            return Uni.createFrom().failure(ex);
        }
        return this.findByKey(key, briefRepresentation);
    }
    
    public Uni<AccountSubscriptionDto> findByKey(@NotNull UUID key, boolean briefRepresentation)
    {
        return accountSubscriptionRepository.findByKey(key, !briefRepresentation /*fetchProviderAndConsumer*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper);
    }
    
    public Uni<AccountSubscriptionDto> findByKey(@NotNull UUID key)
    {
        return this.findByKey(key, true);
    }
    
    public Uni<AccountSubscriptionDto> findByKey(@NotBlank String keyAsString)
    {
        return this.findByKey(keyAsString, true);
    }
    
    public Uni<AccountSubscriptionDto> findById(Integer id, boolean briefRepresentation)
    {
        return accountSubscriptionRepository.findById(id, !briefRepresentation /*fetchProviderAndConsumer*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByAssetKey(@NotBlank String assetKey)
    {
        return accountSubscriptionRepository.findByAssetKey(assetKey)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByAssetKeys(@NotEmpty List<String> assetKeys)
    {
        return accountSubscriptionRepository.findByAssetKeys(assetKeys)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByAsset(@NotBlank String assetPid)
    {
        return accountSubscriptionRepository.findByAsset(assetPid)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByConsumer(@Positive int consumerAccountId)
    {
        return accountSubscriptionRepository.findByConsumer(consumerAccountId)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByConsumerKey(@NotNull UUID consumerAccountKey)
    {
        return accountSubscriptionRepository.findByConsumerKey(consumerAccountKey)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByProvider(@Positive int providerAccountId)
    {
        return accountSubscriptionRepository.findByProvider(providerAccountId)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByProviderKey(@NotNull UUID providerAccountKey)
    {
        return accountSubscriptionRepository.findByProviderKey(providerAccountKey)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndAssetKey(@Positive int consumerAccountId, @NotBlank String assetKey)
    {
        return accountSubscriptionRepository.findByConsumerAndAssetKey(consumerAccountId, assetKey)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndAssetKeys(@Positive int consumerAccountId, @NotEmpty List<String> assetKeys)
    {
        return accountSubscriptionRepository.findByConsumerAndAssetKeys(consumerAccountId, assetKeys)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndAsset(@Positive int consumerAccountId, @NotBlank String assetPid)
    {
        return accountSubscriptionRepository.findByConsumerAndAsset(consumerAccountId, assetPid)
            .map(defaultResultsMapper);
    }
    
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndProvider(@Positive int consumerAccountId, @Positive int providerAccountId)
    {
        return accountSubscriptionRepository.findByConsumerAndProvider(consumerAccountId, providerAccountId)
            .map(defaultResultsMapper);
    }   
}
