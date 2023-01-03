package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import eu.opertusmundi.api_auth.model.AccountDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Named("cachingAccountService")
public class CachingAccountService implements AccountService
{
    @Inject
    @Named("defaultAccountService")
    AccountService delegate;
    
    private static final int CACHE_FIND_BY_KEY__TTL_AFTER_WRITE_SECONDS = 120;
    
    private static final int CACHE_FIND_BY_KEY__MAX_SIZE = 1000;
    
    private final Cache<String, Optional<AccountDto>> cacheFindByKey = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(CACHE_FIND_BY_KEY__TTL_AFTER_WRITE_SECONDS))
        .maximumSize(CACHE_FIND_BY_KEY__MAX_SIZE)
        .build();

    private static final int CACHE_FIND_BY_ID__TTL_AFTER_WRITE_SECONDS = 120;
    
    private static final int CACHE_FIND_BY_ID__MAX_SIZE = 1000;

    private final Cache<String, Optional<AccountDto>> cacheFindById = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(CACHE_FIND_BY_ID__TTL_AFTER_WRITE_SECONDS))
        .maximumSize(CACHE_FIND_BY_ID__MAX_SIZE)
        .build();
    
    private static final int CACHE_FIND_BY_EMAIL__TTL_AFTER_WRITE_SECONDS = 120;
    
    private static final int CACHE_FIND_BY_EMAIL__MAX_SIZE = 1000;
    
    private final Cache<String, Optional<AccountDto>> cacheFindByEmail = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(CACHE_FIND_BY_EMAIL__TTL_AFTER_WRITE_SECONDS))
        .maximumSize(CACHE_FIND_BY_EMAIL__MAX_SIZE)
        .build();
    
    @Override
    public Uni<AccountDto> findByKey(@NotNull UUID key, boolean briefRepresentation)
    {
        final String cacheKey = key.toString() + "/" + (briefRepresentation? "brief" : "full");
        
        final Optional<AccountDto> optionalDto = cacheFindByKey.getIfPresent(cacheKey);
        if (optionalDto != null) {
            return Uni.createFrom().item(optionalDto.orElse(null));
        }
        return delegate.findByKey(key, briefRepresentation)
            .invoke(dto -> cacheFindByKey.put(cacheKey, Optional.ofNullable(dto)));
    }

    @Override
    public Uni<AccountDto> findById(int id,  boolean briefRepresentation)
    {
        final String cacheKey = String.valueOf(id) + "/" + (briefRepresentation? "brief" : "full"); 
        
        final Optional<AccountDto> optionalDto = cacheFindById.getIfPresent(cacheKey);
        if (optionalDto != null) {
            return Uni.createFrom().item(optionalDto.orElse(null));
        }
        return delegate.findById(id, briefRepresentation)
            .invoke(dto -> cacheFindById.put(cacheKey, Optional.ofNullable(dto)));
    }
    
    @Override
    public Uni<AccountDto> findByEmail(@NotBlank String email, boolean briefRepresentation)
    {
        final String cacheKey = email + "/" + (briefRepresentation? "brief" : "full"); 
        
        final Optional<AccountDto> optionalDto = cacheFindByEmail.getIfPresent(cacheKey);
        if (optionalDto != null) {
            return Uni.createFrom().item(optionalDto.orElse(null));
        }
        return delegate.findByEmail(email, briefRepresentation)
            .invoke(dto -> cacheFindByEmail.put(cacheKey, Optional.ofNullable(dto)));
    }
}