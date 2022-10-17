package eu.opertusmundi.api_auth.auth_subrequest.repository;

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
    public Uni<AccountClientEntity> fetch(UUID clientId)
    {
        return this.find("FROM AccountClient c JOIN FETCH c.account WHERE c.clientId = ?1", clientId)
            .singleResult();
    }
}
