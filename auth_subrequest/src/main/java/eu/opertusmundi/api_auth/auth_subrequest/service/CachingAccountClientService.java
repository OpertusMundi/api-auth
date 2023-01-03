package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import eu.opertusmundi.api_auth.model.AccountClientDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Named("cachingAccountClientService")
public class CachingAccountClientService implements AccountClientService
{
    @Inject
    @Named("defaultAccountClientService")
    AccountClientService delegate;
    
    private static final int CACHE_FIND_BY_KEY__TTL_AFTER_WRITE_SECONDS = 120;
    
    private static final int CACHE_FIND_BY_KEY__MAX_SIZE = 2500;
    
    private final Cache<String, Optional<AccountClientDto>> cacheFindByKey = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(CACHE_FIND_BY_KEY__TTL_AFTER_WRITE_SECONDS))
        .maximumSize(CACHE_FIND_BY_KEY__MAX_SIZE)
        .build();
    
    @Override
    public Uni<AccountClientDto> findByKey(@NotNull UUID key, boolean briefRepresentation)
    {
        final String cacheKey = key.toString() + "/" + (briefRepresentation? "brief" : "full");
        final Optional<AccountClientDto> optionalDto = cacheFindByKey.getIfPresent(cacheKey);
        if (optionalDto != null) {
            return Uni.createFrom().item(optionalDto.orElse(null));
        }
        return delegate.findByKey(key, briefRepresentation)
            .invoke(dto -> cacheFindByKey.put(cacheKey, Optional.ofNullable(dto)));
    }
}
