package eu.opertusmundi.api_auth.auth_subrequest.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultLayerNamingStrategy implements LayerNamingStrategy
{
    final Pattern LAYER_PATTERN = Pattern.compile(
        "(?<layerName>" +
            "_" +
            "(?<assetKey>[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})" + ")", 
        Pattern.CASE_INSENSITIVE);
    
    @Override
    public String extractAssetKeyFromLayerName(String layerName)
    {
        final Matcher matcher = LAYER_PATTERN.matcher(layerName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("layer name is malformed: [" + layerName + "]");
        }
        return matcher.group("assetKey");
    }
}
