package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Objects.requireNonNull(layerName);
        final Matcher matcher = LAYER_PATTERN.matcher(layerName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("layer name is malformed: [" + layerName + "]");
        }
        return matcher.group("assetKey");
    }
    
    @Inject
    AssetResourceService assetResourceService;
    
    @Inject
    AccountSubscriptionService accountSubscriptionService;
}
