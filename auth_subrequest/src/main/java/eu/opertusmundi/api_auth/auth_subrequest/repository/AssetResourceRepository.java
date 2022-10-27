package eu.opertusmundi.api_auth.auth_subrequest.repository;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.Validate;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import eu.opertusmundi.api_auth.domain.AssetResourceEntity;

@ApplicationScoped
public class AssetResourceRepository implements PanacheRepositoryBase<AssetResourceEntity, Integer>
{
    @ReactiveTransactional
    public Uni<AssetResourceEntity> findByKey(String key)
    {
        return this.findByKey(key, false);
    }
    
    @ReactiveTransactional
    public Uni<AssetResourceEntity> findByKey(String key, boolean fetchUploadedBy)
    {
        Objects.requireNonNull(key);
        
        final StringBuilder qlBuilder = new StringBuilder("FROM AssetResource r");
        if (fetchUploadedBy) {
            qlBuilder.append(' ');
            qlBuilder.append("LEFT JOIN FETCH r.uploadedBy");
        }
        qlBuilder.append(' ');
        qlBuilder.append("WHERE r.key = ?1");
        
        return this.find(qlBuilder.toString(), key).singleResult();
    }
    
    @ReactiveTransactional
    public Uni<AssetResourceEntity> findByPid(String pid)
    {
        return findByPid(pid, false);
    }
    
    public Uni<List<AssetResourceEntity>> findAllByKey(List<String> keys)
    {
        Objects.requireNonNull(keys);
        Validate.isTrue(!keys.isEmpty(), "keys must not be empty");
        return this.find("key IN ?1", keys).list();
    }
    
    @ReactiveTransactional
    public Uni<AssetResourceEntity> findByPid(String pid, boolean fetchUploadedBy)
    {
        Objects.requireNonNull(pid);
        
        final StringBuilder qlBuilder = new StringBuilder("FROM AssetResource r");
        if (fetchUploadedBy) {
            qlBuilder.append(' ');
            qlBuilder.append("LEFT JOIN FETCH r.uploadedBy");
        }
        qlBuilder.append(' ');
        qlBuilder.append("WHERE r.pid = ?1");
        
        return this.find(qlBuilder.toString(), pid).singleResult();
    }
}
