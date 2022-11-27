package eu.opertusmundi.api_auth.auth_subrequest.model;

import static eu.opertusmundi.api_auth.auth_subrequest.model.RequestType.WMTS;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Represents a WMS request.
 * 
 * @see https://docs.geoserver.org/stable/en/user/services/wms/reference.html
 */
@lombok.Getter
public abstract class WmtsRequest extends OwsRequest
{
    public enum ServiceVersion 
    {
        V_1_0_0("1.0.0");
        
        private final String versionString; 
        
        private ServiceVersion(String versionString)
        {
            this.versionString = versionString;
        }
        
        public String versionAsString()
        {
            return versionString;
        }
        
        public static ServiceVersion fromString(String versionString)
        {
            for (ServiceVersion v: ServiceVersion.values())
                if (v.versionString.equals(versionString))
                    return v;
            return null;
        }
    }
    
    public enum OperationType
    {
        GET_CAPABILITIES("GetCapabilities") {
            @Override
            public WmtsRequest buildRequest(ServiceVersion version)
            {
                return new WmtsGetCapabilitiesRequest(version);
            }
        },
        
        GET_TILE("GetTile") {
            @Override
            public WmtsRequest buildRequest(ServiceVersion version)
            {
                return new WmtsGetTileRequest(version);
            }
        };
        
        private final String operationName;
        
        private OperationType(String name)
        {
            this.operationName = name;
        }
        
        public String operationName()
        {
            return operationName;
        }
        
        public static OperationType fromName(String operationName) 
        {
            for (OperationType t: OperationType.values())
                if (t.operationName.equalsIgnoreCase(operationName))
                    return t;
            return null;
        }
        
        public abstract WmtsRequest buildRequest(ServiceVersion version);
    }
    
    protected final ServiceVersion serviceVersion;
    
    WmtsRequest(ServiceVersion version, OperationType op)
    {
        super(WMTS.name(), version == null? null : version.versionString, op.operationName);
        this.serviceVersion = version;
    }
    
    WmtsRequest(OperationType op)
    {
        this(ServiceVersion.V_1_0_0, op);
    }
    
    public static WmtsRequest fromMap(Map<String, String> queryParameters)
    {
        Validate.notNull(queryParameters, "queryMap must not be null");

        final String service = queryParameters.get(SERVICE_PARAMETER_NAME);
        Validate.isTrue(service == null || service.equalsIgnoreCase(WMTS.name()), 
            "`%s` parameter must either be '%s' or null (which defaults to the same)", 
            SERVICE_PARAMETER_NAME, WMTS.name());
        
        final String operationName = queryParameters.get(REQUEST_PARAMETER_NAME);
        Validate.notBlank(operationName, "%s: must not be blank", REQUEST_PARAMETER_NAME);
        final OperationType operationType = OperationType.fromName(operationName);
        Validate.isTrue(operationType != null, "%s: unsupported operation ([%s])", 
            REQUEST_PARAMETER_NAME, operationName);
        
        final String versionString = queryParameters.get(VERSION_PARAMETER_NAME);
        final ServiceVersion version = StringUtils.isBlank(versionString)? 
            null : ServiceVersion.fromString(versionString);
        
        // build request of the proper subtype (depending on operation)
        final WmtsRequest request = operationType.buildRequest(version);
        // set all other operation-specific stuff
        request.setFromMap(queryParameters);
        
        return request;
    }
}
