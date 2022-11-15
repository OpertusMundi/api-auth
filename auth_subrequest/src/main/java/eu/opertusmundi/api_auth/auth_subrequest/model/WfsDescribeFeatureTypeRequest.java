package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

/**
 * @see https://docs.geoserver.org/latest/en/user/services/wfs/reference.html#describefeaturetype
 */
public class WfsDescribeFeatureTypeRequest extends WfsRequest
{
    @Size(min = 1, max = 25)
    protected List<String> layerNames = Collections.emptyList();

    public static final String TYPENAME_PARAMETER_NAME = "typeName";
    
    public static final String TYPENAMES_PARAMETER_NAME = "typeNames";
    
    public WfsDescribeFeatureTypeRequest(ServiceVersion version)
    {
        super(version, OperationType.DESCRIBE_FEATURE_TYPE);
    }
    
    public WfsDescribeFeatureTypeRequest()
    {
        super(OperationType.DESCRIBE_FEATURE_TYPE);
    }

    @Override
    protected void setFromMap(Map<String, String> queryParameters)
    {
        final String layerNamesAsString = queryParameters.get(serviceVersion == ServiceVersion.V_2_0_0? 
            TYPENAMES_PARAMETER_NAME : TYPENAME_PARAMETER_NAME);
        if (!StringUtils.isBlank(layerNamesAsString)) {
            setLayerNames(StringUtils.split(layerNamesAsString, ','));
        }
    }
    
    void setLayerNames(List<String> layerNames)
    {
        this.layerNames = List.copyOf(Objects.requireNonNull(layerNames));
    }
    
    void setLayerNames(String... layerNames)
    {
        this.layerNames = List.of(layerNames); 
    }
    
    @Override
    public String toString()
    {
        return String.format("%s.%s {version=%s, layers=%s}", 
            service, request, version, layerNames);
    }
}
