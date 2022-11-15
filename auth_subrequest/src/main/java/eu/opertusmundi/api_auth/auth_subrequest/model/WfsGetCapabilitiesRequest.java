package eu.opertusmundi.api_auth.auth_subrequest.model;

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
}
