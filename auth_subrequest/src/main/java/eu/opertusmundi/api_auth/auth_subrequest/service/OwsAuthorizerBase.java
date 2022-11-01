package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

abstract class OwsAuthorizerBase
{
    static final Pattern LAYER_PATTERN = Pattern.compile(
        "(?<layerName>" +
            "_" +
            "(?<assetKey>[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})" + ")", 
        Pattern.CASE_INSENSITIVE);
    
    static String assetKeyFromLayerName(String layerName)
    {
        final Matcher matcher = LAYER_PATTERN.matcher(layerName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("layer name is malformed: [" + layerName + "]");
        }
        return matcher.group("assetKey");
    }
    
    static List<String> assetKeysFromLayerNames(List<String> layerNames)
    {
        if (layerNames.isEmpty())
            return Collections.emptyList();
        else if (layerNames.size() == 1) // optimize common case of single layer
            return Collections.singletonList(assetKeyFromLayerName(layerNames.get(0)));
        
        return layerNames.stream().map(s -> assetKeyFromLayerName(s))
            .collect(Collectors.toUnmodifiableList());
    }
    
    @Inject
    AssetResourceService assetResourceService;
    
    @Inject
    AccountSubscriptionService accountSubscriptionService;
}
