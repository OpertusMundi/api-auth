package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import eu.opertusmundi.api_auth.auth_subrequest.repository.AssetResourceRepository;
import eu.opertusmundi.api_auth.domain.AssetResourceEntity;
import eu.opertusmundi.api_auth.model.AssetResourceDto;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Named("defaultAssetResourceService")
public class DefaultAssetResourceService implements AssetResourceService
{
    @Inject
    AssetResourceRepository assetResourceRepository;
    
    private final Function<AssetResourceEntity, AssetResourceDto> briefToDtoMapper =
        assetResourceEntity -> assetResourceEntity.toDto(false /* convertAccountToDto */);
        
    private final Function<AssetResourceEntity, AssetResourceDto> fullToDtoMapper =
        assetResourceEntity -> assetResourceEntity.toDto(true /* convertAccountToDto */);
    
    @Override
    public Uni<AssetResourceDto> findByKey(@NotBlank String key, boolean briefRepresentation)
    {
        return assetResourceRepository.findByKey(key, !briefRepresentation /*fetchUploadedBy*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper)
            .onFailure(NoResultException.class)
                .transform(ex -> new IllegalStateException("no asset resource for key: [" + key + "]", ex));
    }
    
    @Override
    public Uni<AssetResourceDto> findByKey(@NotBlank String key)
    {
        return this.findByKey(key, true);
    }
    
    @Override
    public Uni<List<AssetResourceDto>> findAllByKey(@NotEmpty List<String> keys)
    {
        return assetResourceRepository.findAllByKey(keys)
            .map(results -> results.stream().map(briefToDtoMapper).collect(Collectors.toUnmodifiableList()));
    }
    
    @Override
    public Uni<Map<String, String>> mapKeyToPid(@NotEmpty List<String> keys)
    {
        return assetResourceRepository.findAllByKey(keys)
            .map(results -> results.stream().collect(
                Collectors.toUnmodifiableMap(AssetResourceEntity::getKey, AssetResourceEntity::getPid)));
    }
    
    @Override
    public Uni<AssetResourceDto> findByPid(@NotBlank String pid, boolean briefRepresentation)
    {
        return assetResourceRepository.findByPid(pid, !briefRepresentation /*fetchUploadedBy*/)
            .map(briefRepresentation? briefToDtoMapper : fullToDtoMapper)
            .onFailure(NoResultException.class)
                .transform(ex -> new IllegalStateException("no asset resource for pid: [" + pid + "]", ex));
    }
    
    @Override
    public Uni<AssetResourceDto> findByPid(@NotBlank String pid)
    {
        return this.findByPid(pid, true);
    }
}
