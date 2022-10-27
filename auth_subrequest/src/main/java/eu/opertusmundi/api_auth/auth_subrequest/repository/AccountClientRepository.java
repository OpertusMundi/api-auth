package eu.opertusmundi.api_auth.auth_subrequest.repository;

import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import eu.opertusmundi.api_auth.domain.AccountClientEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class AccountClientRepository implements PanacheRepositoryBase<AccountClientEntity, Integer>
{
    @ReactiveTransactional
    public Uni<AccountClientEntity> findByKey(UUID key)
    {
        return this.findByKey(key, false);
    }
    
    @ReactiveTransactional
    public Uni<AccountClientEntity> findByKey(UUID key, boolean fetchAccount)
    {
        Objects.requireNonNull(key);
        
        final StringBuilder qlBuilder = new StringBuilder("FROM AccountClient c");
        if (fetchAccount) {
            qlBuilder.append(' ');
            qlBuilder.append("LEFT JOIN FETCH c.account");
        }
        qlBuilder.append(' ');
        qlBuilder.append("WHERE c.key = ?1");
        
        return this.find(qlBuilder.toString(), key).singleResult();
    }
}
