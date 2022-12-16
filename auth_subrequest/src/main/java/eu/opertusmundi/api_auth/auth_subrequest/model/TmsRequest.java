package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.apache.commons.lang3.Validate;

import static eu.opertusmundi.api_auth.auth_subrequest.model.RequestType.TMS;

/**
 * Represents a TMS request
 * 
 * @see https://www.geowebcache.org/docs/current/services/tms.html
 */
@lombok.Getter
@lombok.Builder
public class TmsRequest extends OwsRequest
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
    
    /**
     * Enumeration of TMS output (image) formats.
     */
    public enum OutputFormat
    {
        PNG("image/png", "png"),
        
        JPEG("image/jpeg", "jpeg"),
        
        MAPBOX_VECTOR_TILE("application/vnd.mapbox-vector-tile", "pbf");
        
        final String format;
        
        final String formatExtension;
        
        private OutputFormat(String format, String formatExtension)
        {
            this.format = format;
            this.formatExtension = formatExtension;
        }
        
        public static OutputFormat fromString(String format)
        {
            for (OutputFormat f: OutputFormat.values())
                if (f.format.equals(format) || f.formatExtension.equals(format))
                    return f;
            return null;
        }
        
        public static OutputFormat fromExtension(String formatExtension)
        {
            for (OutputFormat f: OutputFormat.values())
                if (f.formatExtension.equals(formatExtension))
                    return f;
            return null;
        }
        
        public String toFormatString()
        {
            return format;
        }
        
        public String getFormatExtension()
        {
            return formatExtension;
        }
    }
    
    @NotNull
    protected final ServiceVersion serviceVersion;
    
    @NotBlank
    protected final String layerName;

    @NotBlank
    protected final String gridsetId;
    
    @NotNull
    protected final OutputFormat outputFormat;
    
    @PositiveOrZero
    protected final int z;
    
    @PositiveOrZero
    protected final int x;
    
    @PositiveOrZero
    protected final int y;
    
    TmsRequest(
        ServiceVersion serviceVersion, 
        String layerName, 
        String gridsetId, 
        OutputFormat outputFormat,
        int z, int x, int y)
    {
        // Note: TMS does not really have a request field; this empty name is just for satisfying parent constructor
        super(TMS.name(), serviceVersion == null? null : serviceVersion.versionString, "");
        this.serviceVersion = serviceVersion;
        this.layerName = layerName;
        this.gridsetId = gridsetId;
        this.outputFormat = outputFormat;
        this.z = z;
        this.x = x;
        this.y = y;
    }
    
    @Override
    protected void setFromMap(Map<String, String> queryParameters)
    {
        // no-op
    }
    
    @Override
    public List<String> getLayerNames()
    {
        return Collections.singletonList(layerName);
    }
    
    @Override
    public String toString()
    {
        return String.format("%s {version=%s, layer=%s, gridset=%s, format=%s, z=%d, x=%d, y=%d}", 
            service, version, layerName, gridsetId, outputFormat, z, x, y);
    }
}
