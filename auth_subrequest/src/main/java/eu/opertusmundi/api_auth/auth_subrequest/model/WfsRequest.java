package eu.opertusmundi.api_auth.auth_subrequest.model;

import static eu.opertusmundi.api_auth.model.OwsServiceType.WFS;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;


/**
 * Represents a WFS request.
 * 
 * @see https://docs.geoserver.org/latest/en/user/services/wfs/reference.html
 */
public abstract class WfsRequest extends OwsRequest
{
    public enum ServiceVersion 
    {
        V_1_1_0("1.1.0"),
        
        V_2_0_0("2.0.0");
        
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
            public WfsRequest buildRequest(ServiceVersion version)
            {
                return new WfsGetCapabilitiesRequest(version);
            }
        },
        
        DESCRIBE_FEATURE_TYPE("DescribeFeatureType") {
            @Override
            public WfsRequest buildRequest(ServiceVersion version)
            {
                return new WfsDescribeFeatureTypeRequest(version);
            }    
        },
        
        GET_FEATURE("GetFeature") {
            @Override
            public WfsRequest buildRequest(ServiceVersion version)
            {
                return new WfsGetFeatureRequest(version);
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
        
        public abstract WfsRequest buildRequest(ServiceVersion version);
    }
    
    protected final ServiceVersion serviceVersion;
    
    WfsRequest(ServiceVersion version, OperationType op)
    {
        super(WFS, version == null? null : version.versionString, op.operationName);
        this.serviceVersion = version;
    }
    
    WfsRequest(OperationType op)
    {
        this(ServiceVersion.V_2_0_0, op);
    }
    
    public static WfsRequest fromMap(Map<String, String> queryParameters)
    {
        Validate.notNull(queryParameters, "queryMap must not be null");

        final String service = queryParameters.get(SERVICE_PARAMETER_NAME);
        Validate.isTrue(service == null || service.equalsIgnoreCase(WFS.name()), 
            "`%s` parameter must either be '%s' or null (which defaults to the same)", 
            SERVICE_PARAMETER_NAME, WFS.name());
        
        final String operationName = queryParameters.get(REQUEST_PARAMETER_NAME);
        Validate.notBlank(operationName, "%s: must not be blank", REQUEST_PARAMETER_NAME);
        final OperationType operationType = OperationType.fromName(operationName);
        Validate.isTrue(operationType != null, "%s: unsupported operation ([%s])", 
            REQUEST_PARAMETER_NAME, operationName);
        
        final String versionString = queryParameters.get(VERSION_PARAMETER_NAME);
        final ServiceVersion version = StringUtils.isBlank(versionString)? 
            null : ServiceVersion.fromString(versionString);
        
        // build request of the proper subtype (depending on operation)
        final WfsRequest request = operationType.buildRequest(version);
        // set all other operation-specific stuff
        request.setFromMap(queryParameters);
        
        return request;
    }
}
