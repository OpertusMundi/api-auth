package eu.opertusmundi.api_auth.auth_subrequest.repository;

import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import eu.opertusmundi.api_auth.domain.AccountEntity;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class AccountRepository implements PanacheRepositoryBase<AccountEntity, Integer> 
{
    @ReactiveTransactional
    public Uni<AccountEntity> findByKey(UUID key)
    {
        return this.findByKey(key, false);
    }
    
    @ReactiveTransactional
    public Uni<AccountEntity> findByKey(UUID key, boolean fetchAssociatedClients)
    {
        Objects.requireNonNull(key);
        
        PanacheQuery<AccountEntity> q;
        if (fetchAssociatedClients) {
            q = this.find("FROM Account a LEFT JOIN FETCH a.clients WHERE a.key = ?1", key);
        } else {
            q = this.find("FROM Account a WHERE a.key = ?1", key);
        }
        
        return q.singleResult();
    }
    
    @ReactiveTransactional
    public Uni<AccountEntity> findById(Integer id, boolean fetchAssociatedClients)
    {
        if (!fetchAssociatedClients)
            return this.findById(id);
        
        PanacheQuery<AccountEntity> q = 
            this.find("FROM Account a LEFT JOIN FETCH a.clients WHERE a.id = ?1", id);
        return q.singleResult();
    }
    
    @ReactiveTransactional
    public Uni<AccountEntity> findByEmail(String email, boolean fetchAssociatedClients)
    {
        Objects.requireNonNull(email);
        
        PanacheQuery<AccountEntity> q;
        if (fetchAssociatedClients) {
            q = this.find("FROM Account a LEFT JOIN FETCH a.clients WHERE a.email = ?1", email);
        } else {
            q = this.find("FROM Account a WHERE a.email = ?1", email);
        }
        
        return q.singleResult();
    }
}
