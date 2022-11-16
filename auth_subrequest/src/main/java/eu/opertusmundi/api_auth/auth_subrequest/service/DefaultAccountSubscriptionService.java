package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AccountSubscriptionRepository;
import eu.opertusmundi.api_auth.domain.AccountSubscriptionEntity;
import eu.opertusmundi.api_auth.model.AccountSubscriptionDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Named("defaultAccountSubscriptionService")
public class DefaultAccountSubscriptionService implements AccountSubscriptionService
{
    @Inject
    AccountSubscriptionRepository accountSubscriptionRepository;
    
    private final Function<AccountSubscriptionEntity, AccountSubscriptionDto> briefToDtoMapper =
        accountSubscriptionEntity -> accountSubscriptionEntity.toDto(false /* convertAccountToDto */);
        
    private final Function<AccountSubscriptionEntity, AccountSubscriptionDto> fullToDtoMapper =
        accountSubscriptionEntity -> accountSubscriptionEntity.toDto(true /* convertAccountToDto */);
       
    private final Function<AccountSubscriptionEntity, AccountSubscriptionDto> defaultToDtoMapper = 
       USE_BRIEF_REPRESENTATION?  briefToDtoMapper : fullToDtoMapper;
    
    private final Function<List<AccountSubscriptionEntity>, List<AccountSubscriptionDto>> defaultResultsMapper =
        results -> results.stream().map(defaultToDtoMapper).collect(Collectors.toUnmodifiableList());
            
    @Override
    public Uni<AccountSubscriptionDto> findByKey(@NotNull UUID key, boolean briefRepresentation)
    {
        return accountSubscriptionRepository.findByKey(key, !briefRepresentation /*fetchProviderAndConsumer*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper);
    }
    
    @Override
    public Uni<AccountSubscriptionDto> findById(@Positive int id, boolean briefRepresentation)
    {
        return accountSubscriptionRepository.findById(id, !briefRepresentation /*fetchProviderAndConsumer*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByAssetKey(@NotBlank String assetKey)
    {
        return accountSubscriptionRepository.findByAssetKey(assetKey)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByAssetKeys(@NotEmpty List<String> assetKeys)
    {
        return accountSubscriptionRepository.findByAssetKeys(assetKeys)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByAsset(@NotBlank String assetPid)
    {
        return accountSubscriptionRepository.findByAsset(assetPid)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByConsumer(@Positive int consumerAccountId)
    {
        return accountSubscriptionRepository.findByConsumer(consumerAccountId)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByConsumerKey(@NotNull UUID consumerAccountKey)
    {
        return accountSubscriptionRepository.findByConsumerKey(consumerAccountKey)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByProvider(@Positive int providerAccountId)
    {
        return accountSubscriptionRepository.findByProvider(providerAccountId)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByProviderKey(@NotNull UUID providerAccountKey)
    {
        return accountSubscriptionRepository.findByProviderKey(providerAccountKey)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndAssetKey(@Positive int consumerAccountId, @NotBlank String assetKey)
    {
        return accountSubscriptionRepository.findByConsumerAndAssetKey(consumerAccountId, assetKey)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndProviderAndAssetKey(
        @Positive int consumerAccountId, @Positive int providerAccountId, @NotBlank String assetKey)
    {
        return accountSubscriptionRepository.findByConsumerAndProviderAndAssetKey(consumerAccountId, providerAccountId, assetKey)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndAssetKeys(@Positive int consumerAccountId, @NotEmpty List<String> assetKeys)
    {
        return accountSubscriptionRepository.findByConsumerAndAssetKeys(consumerAccountId, assetKeys)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndProviderAndAssetKeys(
        @Positive int consumerAccountId, @Positive int providerAccountId, @NotEmpty List<String> assetKeys)
    {
        return accountSubscriptionRepository.findByConsumerAndProviderAndAssetKeys(consumerAccountId, providerAccountId, assetKeys)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndAsset(@Positive int consumerAccountId, @NotBlank String assetPid)
    {
        return accountSubscriptionRepository.findByConsumerAndAsset(consumerAccountId, assetPid)
            .map(defaultResultsMapper);
    }
    
    @Override
    public Uni<List<AccountSubscriptionDto>> findByConsumerAndProvider(@Positive int consumerAccountId, @Positive int providerAccountId)
    {
        return accountSubscriptionRepository.findByConsumerAndProvider(consumerAccountId, providerAccountId)
            .map(defaultResultsMapper);
    }   
}
