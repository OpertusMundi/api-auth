package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WmtsGetCapabilitiesRequest extends WmtsRequest
{
    public WmtsGetCapabilitiesRequest(ServiceVersion serviceVersion)
    {
        super(serviceVersion, OperationType.GET_CAPABILITIES);
    }
    
    public WmtsGetCapabilitiesRequest()
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
