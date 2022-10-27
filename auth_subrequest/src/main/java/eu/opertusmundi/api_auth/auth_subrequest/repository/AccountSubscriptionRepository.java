package eu.opertusmundi.api_auth.auth_subrequest.repository;

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
            q = this.find("FROM AccountSubscription s" +
                " JOIN FETCH s.consumer JOIN FETCH s.provider WHERE s.key = ?1", key);
        } else {
            q = this.find("FROM AccountSubscription s WHERE s.key = ?1", key);
        }
        
        return q.singleResult();
    }
    
    @ReactiveTransactional
    public Uni<AccountSubscriptionEntity> findById(Integer id, boolean fetchProviderAndConsumer)
    {
        if (!fetchProviderAndConsumer)
            return this.findById(id);
        
        PanacheQuery<AccountSubscriptionEntity> q = this.find("FROM AccountSubscription s" +
            " JOIN FETCH s.consumer JOIN FETCH s.provider WHERE s.id = ?1", id);
        return q.singleResult();
    }
    
    ////////////////////////////////
    ////////// FIXME Tests /////////
    ////////////////////////////////
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumerKey(UUID consumerAccountKey)
    {
        return this.find(
                "FROM AccountSubscription s JOIN FETCH s.consumer c " +
                "WHERE c.key = ?1", consumerAccountKey)
            .list();
    }
    
    @ReactiveTransactional
    public Uni<List<AccountSubscriptionEntity>> findByConsumerClientKey(UUID consumerClientKey)
    {
        return this.find(
                "FROM AccountSubscription s JOIN FETCH s.consumer x JOIN FETCH x.clients c " +
                "WHERE c.key = ?1", consumerClientKey)
            .list();
    }
}
