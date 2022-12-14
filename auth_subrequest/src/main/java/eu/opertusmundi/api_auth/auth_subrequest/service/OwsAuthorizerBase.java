package eu.opertusmundi.api_auth.auth_subrequest.service;

import javax.inject.Inject;

abstract class OwsAuthorizerBase
{
    @Inject
    LayerNamingStrategy layerNamingStrategy;
    
    @Inject
    AssetResourceService assetResourceService;
}
