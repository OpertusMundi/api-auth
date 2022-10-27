package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @see https://docs.geoserver.org/stable/en/user/services/wms/reference.html#getmap
 */
@lombok.Getter
public class WmsGetMapRequest extends WmsRequest
{
    /**
     * Enumeration of WMS binary (image) formats.
     * 
     * @see https://docs.geoserver.org/stable/en/user/services/wms/outputformats.html#wms-output-formats
     */
    public enum OutputFormat
    {
        PNG("image/png"),
        
        PNG8("image/png8"),
        
        JPEG("image/jpeg"),
        
        JPEG_PNG("image/vnd.jpeg-png"),
        
        JPEG_PNG8("image/vnd.jpeg-png8"),
        
        GIF("image/gif"),
        
        TIFF("image/tiff"),
        
        TIFF8("image/tiff8"),
        
        GEOTIFF("image/geotiff"),
        
        GEOTIFF8("image/geotiff8"),
        
        SVG("image/svg"),
        
        PDF("application/pdf"),
        
        GEORSS("rss"),
        
        KML("kml"),
        
        KMZ("kmz"),
        
        OPENLAYERS("application/openlayers"),
        
        UTFGRID("application/json;type=utfgrid");
        
        final String format;
        
        private OutputFormat(String format)
        {
            this.format = format;
        }
        
        public static OutputFormat fromString(String format)
        {
            for (OutputFormat f: OutputFormat.values())
                if (f.format.equals(format))
                    return f;
            return null;
        }
        
        public String toFormatString()
        {
            return format;
        }
    }
    
    @lombok.Getter
    @lombok.AllArgsConstructor(staticName = "of")
    public static class BBox 
    {
        final double minx, miny, maxx, maxy;
        
        static BBox fromString(String bboxAsString)
        {
            Validate.notBlank(bboxAsString);
            final String[] coords = StringUtils.split(bboxAsString, ',');
            Validate.isTrue(coords.length == 4, "expected BBox format: minx,miny,maxx,maxy");
            
            double minx = Double.parseDouble(coords[0]);
            double miny = Double.parseDouble(coords[1]);
            double maxx = Double.parseDouble(coords[2]);
            double maxy = Double.parseDouble(coords[3]);
            
            return new BBox(minx, miny, maxx, maxy);
        }

        @Override
        public String toString()
        {
            return String.format("{minx=%.4f, miny=%.4f, maxx=%.4f, maxy=%.4f}", minx, miny, maxx, maxy);
        }
    }
    
    protected List<String> layerNames = Collections.emptyList();

    public static final String LAYERS_PARAMETER_NAME = "layers";
    
    protected List<String> styleNames = Collections.emptyList();
    
    public static final String STYLES_PARAMETER_NAME = "styles";
    
    protected String srs;
    
    public static final String SRS_PARAMETER_NAME = "srs";
    
    public static final String CRS_PARAMETER_NAME = "crs";
    
    protected static final Predicate<String> SRS_VALIDATOR = 
        Pattern.compile("epsg:([0-9]+)", Pattern.CASE_INSENSITIVE).asMatchPredicate();
    
    protected BBox bbox;
    
    public static final String BBOX_PARAMETER_NAME = "bbox";
    
    protected int width;
    
    public static final String WIDTH_PARAMETER_NAME = "width";
    
    protected int height;
    
    public static final String HEIGHT_PARAMETER_NAME = "height";
    
    protected String format = OutputFormat.PNG.format;
    
    public static final String FORMAT_PARAMETER_NAME = "format";
    
    protected Boolean transparent; 
    
    public static final String TRANSPARENT_PARAMETER_NAME = "transparent";
    
    protected String bgcolor;
    
    public static final String BGCOLOR_PARAMETER_NAME = "bgcolor";
    
    protected static final Predicate<String> COLOR_RRGGBB_VALIDATOR =
        Pattern.compile("0x\\p{XDigit}{6}", Pattern.CASE_INSENSITIVE).asMatchPredicate();
    
    protected URI sld;
    
    public static final String SLD_PARAMETER_NAME = "sld";
    
    protected String sldBody;
    
    public static final String SLDBODY_PARAMETER_NAME = "sld_body";
    
    public WmsGetMapRequest(WmsRequest.ServiceVersion version)
    {
        super(version, WmsRequest.OperationType.GET_MAP);
    }
    
    public WmsGetMapRequest()
    {
        super(WmsRequest.OperationType.GET_MAP);
    }

    @Override
    protected void setFromMap(Map<String, String> queryParameters)
    {
        final String layerNamesAsString = queryParameters.get(LAYERS_PARAMETER_NAME);
        if (!StringUtils.isBlank(layerNamesAsString))
            setLayerNames(StringUtils.split(layerNamesAsString, ','));
        
        final String styleNamesAsString = queryParameters.get(STYLES_PARAMETER_NAME);
        if (!StringUtils.isBlank(styleNamesAsString))
            setStyleNames(StringUtils.split(styleNamesAsString, ','));
        
        final String srs = queryParameters.containsKey(SRS_PARAMETER_NAME)? 
            queryParameters.get(SRS_PARAMETER_NAME) : queryParameters.get(CRS_PARAMETER_NAME);
        if (!StringUtils.isBlank(srs))
            setSrs(srs);
        
        final String bboxAsString = queryParameters.get(BBOX_PARAMETER_NAME);
        if (!StringUtils.isBlank(bboxAsString))
            setBBox(BBox.fromString(bboxAsString));
        
        final String widthAsString = queryParameters.get(WIDTH_PARAMETER_NAME);
        if (!StringUtils.isBlank(widthAsString))
            setWidth(Integer.parseInt(widthAsString));
        
        final String heightAsString = queryParameters.get(HEIGHT_PARAMETER_NAME);
        if (!StringUtils.isBlank(heightAsString))
            setHeight(Integer.parseInt(heightAsString));
        
        final String formatAsString = queryParameters.get(FORMAT_PARAMETER_NAME);
        if (!StringUtils.isBlank(formatAsString))
            setFormat(formatAsString);
        
        final String transparentAsString = queryParameters.get(TRANSPARENT_PARAMETER_NAME);
        if (!StringUtils.isBlank(transparentAsString))
            setTransparent(Boolean.valueOf(transparentAsString));
        
        final URI sld = Optional.ofNullable(queryParameters.get(SLD_PARAMETER_NAME))
            .filter(StringUtils::isNotBlank).map(URI::create).orElse(null);
        if (sld != null)
            setSld(sld);
        
        final String sldBody = queryParameters.get(SLDBODY_PARAMETER_NAME);
        if (!StringUtils.isBlank(sldBody))
            setSldBody(sldBody);
    }
    
    void setLayerNames(List<String> layerNames)
    {
        this.layerNames = List.copyOf(Objects.requireNonNull(layerNames));
    }
    
    void setLayerNames(String... layerNames)
    {
        this.layerNames = List.of(layerNames); 
    }
    
    void setStyleNames(List<String> styleNames)
    {
        this.styleNames = List.copyOf(Objects.requireNonNull(styleNames));
    }
    
    void setStyleNames(String... styleNames)
    {
        this.styleNames = List.of(styleNames); 
    }
    
    void setSrs(String srs)
    {
        Objects.requireNonNull(srs);
        Validate.isTrue(SRS_VALIDATOR.test(srs), "invalid spatial reference system: [%s]", srs);
        this.srs = srs;
    }
    
    void setBBox(BBox bbox)
    {
        this.bbox = Objects.requireNonNull(bbox);
    }
    
    void setWidth(int width)
    {
        this.width = width;
    }
    
    void setHeight(int height)
    {
        this.height = height;
    }
    
    void setFormat(OutputFormat f)
    {
        this.format = Objects.requireNonNull(f).format;
    }
    
    void setFormat(String formatAsString)
    {
        final OutputFormat outputFormat = OutputFormat.fromString(Objects.requireNonNull(formatAsString));
        Validate.isTrue(outputFormat != null, "unsupported output format: [%s]", formatAsString);
        this.format = outputFormat.format;
    }
    
    void setTransparent(Boolean transparent)
    {
        this.transparent = transparent;
    }
    
    void setBgcolor(String color)
    {
        Objects.requireNonNull(color);
        Validate.isTrue(COLOR_RRGGBB_VALIDATOR.test(color), "invalid RRGGBB color: [%s]", color);
        this.bgcolor = color;
    }
    
    void setSld(URI sld)
    {
        this.sld = Objects.requireNonNull(sld);
    }
    
    void setSldBody(String sldBody)
    {
        this.sldBody = Objects.requireNonNull(sldBody);
    }
    
    @Override
    public String toString()
    {
        return String.format("%s.%s {version=%s, layers=%s, srs=%s, bbox=%s}", 
            service, request, version, layerNames, srs, bbox);
    }
}
