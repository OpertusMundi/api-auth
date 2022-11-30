package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import lombok.NonNull;

/**
 * @see https://docs.geoserver.org/latest/en/user/services/wfs/reference.html#getfeature
 */
@lombok.Getter
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
    
    protected static final Predicate<String> SRS_VALIDATOR = 
        Pattern.compile("epsg:([0-9]+)", Pattern.CASE_INSENSITIVE).asMatchPredicate();

    @lombok.Getter
    @lombok.AllArgsConstructor(staticName = "of")
    public static class BBox 
    {
        final double minx, miny, maxx, maxy;
        
        final String srs;
        
        static BBox fromString(String bboxAsString)
        {
            Validate.notBlank(bboxAsString);
            final String[] coords = StringUtils.split(bboxAsString, ',');
            Validate.isTrue(coords.length == 4 || coords.length == 5, 
                "expected BBox format: minx,miny,maxx,maxy[,srs]");
            
            double minx = Double.parseDouble(coords[0]);
            double miny = Double.parseDouble(coords[1]);
            double maxx = Double.parseDouble(coords[2]);
            double maxy = Double.parseDouble(coords[3]);
            
            String srs = null; 
            if (coords.length == 5) {
                srs = coords[4];
                Validate.isTrue(SRS_VALIDATOR.test(srs), "invalid spatial reference system: [%s]", srs);
            }
            
            return new BBox(minx, miny, maxx, maxy, srs);
        }

        @Override
        public String toString()
        {
            return String.format("{minx=%.4f, miny=%.4f, maxx=%.4f, maxy=%.4f, srs=%s}", 
                minx, miny, maxx, maxy, srs);
        }
    }
    
    @Size(min = 1, max = 25)
    protected List<String> layerNames = Collections.emptyList();

    public static final String LAYER_NAMES_V1_PARAMETER_NAME = "typeName";
    
    public static final String LAYER_NAMES_V2_PARAMETER_NAME = "typeNames";
    
    @NonNull
    protected OutputFormat outputFormat = OutputFormat.GML3;
    
    public static final String OUTPUTFORMAT_PARAMETER_NAME = "outputFormat";
    
    protected String featureId;
    
    public static final String FEATUREID_PARAMETER_NAME = "featureID";
    
    protected Integer maxNumOfFeatures;
    
    public static final String MAX_NUM_FEATURES_V1_PARAMETER_NAME = "maxFeatures";
    
    public static final String MAX_NUM_FEATURES_V2_PARAMETER_NAME = "count";
    
    protected List<String> propertyNames;
    
    public static final String PROPERTY_NAMES_PARAMETER_NAME = "propertyName";
    
    protected String srs;
    
    public static final String SRS_PARAMETER_NAME = "srsName";
    
    protected BBox bbox;
        
    public static final String BBOX_PARAMETER_NAME = "bbox";
    
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
        final String layerNamesAsString = queryParameters.get(serviceVersion == ServiceVersion.V_2_0_0? 
            LAYER_NAMES_V2_PARAMETER_NAME : LAYER_NAMES_V1_PARAMETER_NAME);
        if (!StringUtils.isBlank(layerNamesAsString))
            setLayerNames(StringUtils.split(layerNamesAsString, ','));
        
        final String formatAsString = queryParameters.get(OUTPUTFORMAT_PARAMETER_NAME);
        if (!StringUtils.isBlank(formatAsString))
            setOutputFormat(formatAsString);
        
        final String featureId = queryParameters.get(FEATUREID_PARAMETER_NAME);
        if (!StringUtils.isBlank(featureId))
            setFeatureId(featureId);
        
        final String maxNumFeaturesAsString = queryParameters.get(serviceVersion == ServiceVersion.V_2_0_0?
            MAX_NUM_FEATURES_V2_PARAMETER_NAME : MAX_NUM_FEATURES_V1_PARAMETER_NAME);
        if (!StringUtils.isBlank(maxNumFeaturesAsString))
            setMaxNumOfFeatures(Integer.parseInt(maxNumFeaturesAsString));
        
        final String propertyNamesAsString = queryParameters.get(PROPERTY_NAMES_PARAMETER_NAME);
        if (!StringUtils.isBlank(propertyNamesAsString))
            setPropertyNames(StringUtils.split(propertyNamesAsString, ','));
        
        final String srs = queryParameters.get(SRS_PARAMETER_NAME); 
        if (!StringUtils.isBlank(srs))
            setSrs(srs);
        
        final String bboxAsString = queryParameters.get(BBOX_PARAMETER_NAME);
        if (!StringUtils.isBlank(bboxAsString)) 
            setBbox(BBox.fromString(bboxAsString));
    }
    
    public void setOutputFormat(OutputFormat f)
    {
        this.outputFormat = Objects.requireNonNull(f);
    }
    
    public void setOutputFormat(String formatAsString)
    {
        final OutputFormat outputFormat = OutputFormat.fromString(Objects.requireNonNull(formatAsString));
        Validate.isTrue(outputFormat != null, "unsupported output format: [%s]", formatAsString);
        this.outputFormat = outputFormat;
    }
    
    public void setLayerNames(List<String> layerNames)
    {
        this.layerNames = List.copyOf(Objects.requireNonNull(layerNames));
    }
    
    public void setLayerNames(String... layerNames)
    {
        this.layerNames = List.of(layerNames); 
    }
    
    public void setFeatureId(String featureId)
    {
        this.featureId = Objects.requireNonNull(featureId);
    }
    
    public void setMaxNumOfFeatures(int maxNumOfFeatures)
    {
        this.maxNumOfFeatures = Integer.valueOf(maxNumOfFeatures);
    }
    
    public void setPropertyNames(List<String> propertyNames)
    {
        this.propertyNames = propertyNames == null? null : List.copyOf(propertyNames);
    }
    
    public void setPropertyNames(String... propertyNames)
    {
        this.propertyNames = List.of(propertyNames);
    }
    
    public void setSrs(String srs)
    {
        Objects.requireNonNull(srs);
        Validate.isTrue(SRS_VALIDATOR.test(srs), "invalid spatial reference system: [%s]", srs);
        this.srs = srs;
    }
    
    public void setBbox(BBox bbox)
    {
        this.bbox = Objects.requireNonNull(bbox);
    }
        
    @Override
    public String toString()
    {
        return String.format("%s.%s {version=%s, "
            + "layers=%s, outputFormat=%s, srs=%s, bbox=%s, featureId=%s, maxNumOfFeatures=%s, propertyNames=%s}", 
            service, request, version, 
            layerNames, outputFormat, srs, bbox, featureId, maxNumOfFeatures, propertyNames);
    }
}
