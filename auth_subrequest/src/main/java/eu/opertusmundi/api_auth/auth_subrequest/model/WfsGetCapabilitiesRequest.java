package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WfsGetCapabilitiesRequest extends WfsRequest
{
    public WfsGetCapabilitiesRequest(ServiceVersion version)
    {
        super(version, OperationType.GET_CAPABILITIES);
    }
    
    public WfsGetCapabilitiesRequest()
    {
        super(OperationType.GET_CAPABILITIES);
    }

    @Override
    protected void setFromMap(Map<String, String> queryParameters)
    {
        // no-op
    }
    
    @Override
    public List<String> getLayerNames()
    {
        return Collections.emptyList();
    }
}
