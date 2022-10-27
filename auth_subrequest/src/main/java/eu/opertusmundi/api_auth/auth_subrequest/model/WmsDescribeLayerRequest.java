package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @see https://docs.geoserver.org/stable/en/user/services/wms/reference.html#describelayer
 */
@lombok.Getter
public class WmsDescribeLayerRequest extends WmsRequest
{
    public enum OutputFormat
    {
        TEXT("text/xml"),
        GML2("application/vnd.ogc.wms_xml"),
        JSON("application/json");
        
        private final String format;
        
        private OutputFormat(String format)
        {
            this.format = format;
        }
        
        public String toFormatString()
        {
            return format;
        }
        
        public static OutputFormat fromString(String format)
        {
            for (OutputFormat f: OutputFormat.values())
                if (f.format.equals(format))
                    return f;
            return null;
        }
    }
    
    protected List<String> layerNames = Collections.emptyList();

    public static final String LAYERS_PARAMETER_NAME = "layers";
    
    protected String outputFormat = OutputFormat.TEXT.format;
    
    public static final String OUTPUTFORMAT_PARAMETER_NAME = "output_format";
    
    public WmsDescribeLayerRequest(WmsRequest.ServiceVersion version)
    {
        super(version, WmsRequest.OperationType.DESCRIBE_LAYER);
    }
    
    public WmsDescribeLayerRequest()
    {
        super(WmsRequest.OperationType.DESCRIBE_LAYER);
    }

    @Override
    protected void setFromMap(Map<String, String> queryParameters)
    {
        final String layerNamesAsString = queryParameters.get(LAYERS_PARAMETER_NAME);
        if (!StringUtils.isBlank(layerNamesAsString))
            setLayerNames(StringUtils.split(layerNamesAsString, ','));
        
        final String outputFormatAsString = queryParameters.get(OUTPUTFORMAT_PARAMETER_NAME);
        if (!StringUtils.isBlank(outputFormatAsString)) {
            final OutputFormat outputFormat = OutputFormat.fromString(outputFormatAsString);
            Validate.isTrue(outputFormat != null, "%s: unsupported format: [%s]", 
                OUTPUTFORMAT_PARAMETER_NAME, outputFormatAsString);
            setOutputFormat(outputFormat);
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
    
    void setOutputFormat(OutputFormat outputFormat)
    {
        this.outputFormat = Objects.requireNonNull(outputFormat).format;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s.%s {version=%s, layers=%s}", 
            service, request, version, layerNames);
    }
}
