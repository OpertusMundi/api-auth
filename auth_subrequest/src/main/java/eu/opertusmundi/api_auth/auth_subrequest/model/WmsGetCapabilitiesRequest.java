package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Map;

@lombok.Getter
public class WmsGetCapabilitiesRequest extends WmsRequest
{
    public WmsGetCapabilitiesRequest(ServiceVersion version)
    {
        super(version, OperationType.GET_CAPABILITIES);
    }
    
    public WmsGetCapabilitiesRequest()
    {
        super(OperationType.GET_CAPABILITIES);
    }

    @Override
    protected void setFromMap(Map<String, String> queryParameters)
    {
        // no-op
    }
}
