package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Represents a base OWS (WMS/WMTS/WFS) request
 */
@lombok.Getter
@lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class OwsRequest extends Request
{
    /**
     * The service type (e.g. `WMS`)
     */
    protected final OwsServiceType service;
 
    public static final String SERVICE_PARAMETER_NAME = "service";
    
    /**
     * The service-specific version
     */
    @NotBlank
    protected final String version;
    
    public static final String VERSION_PARAMETER_NAME = "version";
    
    /**
     * The service-specific operation name (e.g. `GetMap` for a WMS service), 
     * called <code>request</code> in OGC terminology
     */
    @NotNull
    protected final String request;
    
    public static final String REQUEST_PARAMETER_NAME = "request";
    
    protected abstract void setFromMap(Map<String, String> queryParameters);
    
    @Override
    public String toString()
    {
        return String.format("%s.%s {version=%s}", service, request, version);
    }
    
    public abstract List<String> getLayerNames();
}
