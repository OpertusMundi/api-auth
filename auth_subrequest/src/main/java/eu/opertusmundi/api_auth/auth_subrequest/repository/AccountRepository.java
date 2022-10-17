package eu.opertusmundi.api_auth.auth_subrequest.repository;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import eu.opertusmundi.api_auth.domain.AccountEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class AccountRepository implements PanacheRepositoryBase<AccountEntity, Integer> 
{
    @ReactiveTransactional
    public Uni<AccountEntity> findByKey(UUID key)
    {
        return this.find("a.key = ?1", key)
            .singleResult();
    }
    
    @ReactiveTransactional
    public Uni<AccountEntity> fetchByKey(UUID key)
    {
        return this.find("FROM Account a LEFT JOIN FETCH a.clients WHERE a.key = ?1", key)
            .singleResult();
    }
    
    @ReactiveTransactional
    public Uni<AccountEntity> fetchById(int id)
    {
        return this.find("FROM Account a LEFT JOIN FETCH a.clients WHERE a.id = ?1", id)
            .singleResult();
    }
}
