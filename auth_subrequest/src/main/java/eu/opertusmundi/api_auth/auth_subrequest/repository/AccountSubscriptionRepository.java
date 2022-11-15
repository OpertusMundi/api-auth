package eu.opertusmundi.api_auth.auth_subrequest.repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import eu.opertusmundi.api_auth.domain.AccountSubscriptionEntity;

@ApplicationScoped
public class AccountSubscriptionRepository implements PanacheRepositoryBase<AccountSubscriptionEntity, Integer>
{
    @ReactiveTransactional
    public Uni<AccountSubscriptionEntity> findByKey(UUID key) 
    {
        return this.findByKey(key, false);
    }
    
    @ReactiveTransactional
    public Uni<AccountSubscriptionEntity> findByKey(UUID key, boolean fetchProviderAndConsumer)
    {
        Objects.requireNonNull(key);
        
        PanacheQuery<AccountSubscriptionEntity> q;
        if (fetchProviderAndConsumer) {
            q = this.find("FROM AccountSubscription s JOIN FETCH s.consumer JOIN FETCH s.provider WHERE s.key = ?1", key);
        } else {
            q = this.find("FROM AccountSubscription s WHERE s.key = ?1", key);
        }
        
        return q.singleResult();
    }
    
    @ReactiveTransactional
    public Uni<AccountSubscriptionEntity> findById(int id, boolean fetchProviderAndConsumer)
    {
        if (!fetchProviderAndConsumer)
            return this.findById(id);
        
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.consumer JOIN FETCH s.provider WHERE s.id = ?1", id);
        return q.singleResult();
    }
        
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByAssetKey(String assetKey)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE a.key = ?1", assetKey); 
        return q.list();
    }
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByAssetKeys(List<String> assetKeys)
    {
        if (assetKeys.isEmpty())
            return Uni.createFrom().item(Collections::emptyList);
        else if (assetKeys.size() == 1) // prefer a simpler WHERE clause  
            return this.findByAssetKey(assetKeys.get(0)); 
        
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE a.key IN (?1)", assetKeys); 
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByAsset(String assetPid)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE a.pid = ?1", assetPid);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumer(int consumerAccountId)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE s.consumer.id = ?1", consumerAccountId);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumerKey(UUID consumerAccountKey)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a JOIN FETCH s.consumer c WHERE c.key = ?1", consumerAccountKey);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByProvider(int providerAccountId)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE s.provider.id = ?1", providerAccountId);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByProviderKey(UUID providerAccountKey)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a JOIN FETCH s.provider p WHERE p.key = ?1", providerAccountKey);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumerAndAssetKey(int consumerAccountId, String assetKey)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE s.consumer.id = ?1 AND a.key = ?2", 
            consumerAccountId, assetKey);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumerAndProviderAndAssetKey(
        int consumerAccountId, int providerAccountId, String assetKey)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE s.consumer.id = ?1 AND s.provider.id = ?2 AND a.key = ?3", 
             consumerAccountId, providerAccountId, assetKey);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumerAndAssetKeys(int consumerAccountId, List<String> assetKeys)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE s.consumer.id = ?1 AND a.key IN (?2)", 
            consumerAccountId, assetKeys);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumerAndProviderAndAssetKeys(
        int consumerAccountId, int providerAccountId, List<String> assetKeys)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE s.consumer.id = ?1 AND s.provider.id = ?2 AND a.key IN (?3)", 
            consumerAccountId, providerAccountId, assetKeys);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumerAndAsset(int consumerAccountId, String assetPid)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE s.consumer.id = ?1 AND a.pid = ?2", 
            consumerAccountId, assetPid);
        return q.list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumerAndProvider(int consumerAccountId, int providerAccountId)
    {
        PanacheQuery<AccountSubscriptionEntity> q = this.find(
            "FROM AccountSubscription s JOIN FETCH s.asset a WHERE s.consumer.id = ?1 AND s.provider.id = ?2", 
            consumerAccountId, providerAccountId);
        return q.list();
    }
}
