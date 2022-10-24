package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Map;

@lombok.Getter
@lombok.ToString(callSuper = true)
public class WmsGetCapabilitiesRequest extends WmsRequest
{
    public WmsGetCapabilitiesRequest(WmsRequest.ServiceVersion version)
    {
        super(version, WmsRequest.OperationType.GET_CAPABILITIES);
    }
    
    public WmsGetCapabilitiesRequest()
    {
        super(WmsRequest.OperationType.GET_CAPABILITIES);
    }

    @Override
    protected void setFromMap(Map<String, String> queryParameters)
    {
        // no-op
    }
}
