package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.List;
import java.util.Map;

public class WfsGetFeatureRequest extends WfsRequest
{
    /**
     * Enumeration of WFS output formats for `GetFeature` requests
     * 
     * @see https://docs.geoserver.org/latest/en/user/services/wfs/outputformats.html
     */
    public enum OutputFormat
    {
        GML2("GML2"),
        
        GML3("GML3"),
        
        SHAPEFILE("shape-zip"),
        
        JSON("application/json", "json"),
        
        CSV("csv")
        ;
        
        final String format;
        
        final List<String> aliases;
        
        private OutputFormat(String format, String... aliases)
        {
            this.format = format;
            this.aliases = List.of(aliases);
        }
        
        public static OutputFormat fromString(String format)
        {
            for (OutputFormat f: OutputFormat.values()) {
                if (f.format.equalsIgnoreCase(format))
                    return f;
                for (String alias: f.aliases)
                    if (alias.equalsIgnoreCase(format))
                        return f;
            }
            return null;
        }
        
        public String toFormatString()
        {
            return format;
        }
    }
    
    public WfsGetFeatureRequest(ServiceVersion version)
    {
        super(version, OperationType.GET_FEATURE);
    }
    
    public WfsGetFeatureRequest()
    {
        super(OperationType.GET_FEATURE);
    }
    
    @Override
    protected void setFromMap(Map<String, String> queryParameters)
    {
        // TODO
    }
}
