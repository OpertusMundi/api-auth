package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface LayerNamingStrategy
{
    String extractAssetKeyFromLayerName(String layerName);
    
    default List<String> extractAssetKeysFromLayerNames(List<String> layerNames)
    {
        if (layerNames.isEmpty())
            return Collections.emptyList();
        else if (layerNames.size() == 1) // optimize common case of single layer
            return Collections.singletonList(extractAssetKeyFromLayerName(layerNames.get(0)));
        return layerNames.stream().map(s -> extractAssetKeyFromLayerName(s))
            .collect(Collectors.toUnmodifiableList());
    }
}
