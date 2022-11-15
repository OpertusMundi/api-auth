package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import eu.opertusmundi.api_auth.model.AssetResourceDto;
import io.smallrye.mutiny.Uni;

public interface AssetResourceService
{
    Uni<AssetResourceDto> findByKey(@NotBlank String key, boolean briefRepresentation);

    Uni<AssetResourceDto> findByKey(@NotBlank String key);

    Uni<List<AssetResourceDto>> findAllByKey(@NotEmpty List<String> keys);

    Uni<Map<String, String>> mapKeyToPid(@NotEmpty List<String> keys);

    Uni<AssetResourceDto> findByPid(@NotBlank String pid, boolean briefRepresentation);

    Uni<AssetResourceDto> findByPid(@NotBlank String pid);
}