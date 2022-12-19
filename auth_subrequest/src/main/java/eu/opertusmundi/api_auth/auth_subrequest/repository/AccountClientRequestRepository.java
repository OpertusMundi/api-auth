package eu.opertusmundi.api_auth.auth_subrequest.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

import eu.opertusmundi.api_auth.domain.AccountClientRequestEntity;
import eu.opertusmundi.api_auth.model.AccountClientRequestDto;

@ApplicationScoped
public class AccountClientRequestRepository implements PanacheRepositoryBase<AccountClientRequestEntity, String>
{
    @ReactiveTransactional
    public Uni<AccountClientRequestEntity> createFromDto(AccountClientRequestDto dto)
    {
        AccountClientRequestEntity entity = new AccountClientRequestEntity(dto);
        return this.persist(entity);
    }
}
