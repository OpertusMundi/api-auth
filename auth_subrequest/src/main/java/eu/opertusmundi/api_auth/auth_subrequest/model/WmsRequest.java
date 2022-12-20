package eu.opertusmundi.api_auth.auth_subrequest.model;

import static eu.opertusmundi.api_auth.model.OwsServiceType.WMS;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Represents a WMS request.
 * 
 * @see https://docs.geoserver.org/stable/en/user/services/wms/reference.html
 */
@lombok.Getter
public abstract class WmsRequest extends OwsRequest
{
    public enum ServiceVersion 
    {
        V_1_1_1("1.1.1"),
        
        V_1_3_0("1.3.0");
        
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
            public WmsRequest buildRequest(ServiceVersion version)
            {
                return new WmsGetCapabilitiesRequest(version);
            }
        },
        
        GET_MAP("GetMap") {
            @Override
            public WmsRequest buildRequest(ServiceVersion version)
            {
                return new WmsGetMapRequest(version);
            }
        },
        
        DESCRIBE_LAYER("DescribeLayer") {
            @Override
            public WmsRequest buildRequest(ServiceVersion version)
            {
                return new WmsDescribeLayerRequest(version);
            }
        },
        
        GET_LEGEND_GRAPHIC("GetLegendGraphic") {
            @Override
            public WmsRequest buildRequest(ServiceVersion version)
            {
                return new WmsGetLegendGraphicRequest(version);
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
        
        public abstract WmsRequest buildRequest(ServiceVersion version);
    }
    
    protected final ServiceVersion serviceVersion;
    
    WmsRequest(ServiceVersion version, OperationType op)
    {
        super(WMS, version == null? null : version.versionString, op.operationName);
        this.serviceVersion = version;
    }
    
    WmsRequest(OperationType op)
    {
        this(ServiceVersion.V_1_1_1, op);
    }
    
    public static WmsRequest fromMap(Map<String, String> queryParameters)
    {
        Validate.notNull(queryParameters, "queryMap must not be null");

        final String service = queryParameters.get(SERVICE_PARAMETER_NAME);
        Validate.isTrue(service == null || service.equalsIgnoreCase(WMS.name()), 
            "`%s` parameter must either be '%s' or null (which defaults to the same)", 
            SERVICE_PARAMETER_NAME, WMS.name());
        
        final String operationName = queryParameters.get(REQUEST_PARAMETER_NAME);
        Validate.notBlank(operationName, "%s: must not be blank", REQUEST_PARAMETER_NAME);
        final OperationType operationType = OperationType.fromName(operationName);
        Validate.isTrue(operationType != null, "%s: unsupported operation ([%s])", 
            REQUEST_PARAMETER_NAME, operationName);
        
        final String versionString = queryParameters.get(VERSION_PARAMETER_NAME);
        final ServiceVersion version = StringUtils.isBlank(versionString)? 
            null : ServiceVersion.fromString(versionString);
        
        // build request of the proper subtype (depending on operation)
        final WmsRequest request = operationType.buildRequest(version);
        // set all other operation-specific stuff
        request.setFromMap(queryParameters);
        
        return request;
    }
}
