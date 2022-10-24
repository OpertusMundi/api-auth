package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetMapRequest.OutputFormat;

/**
 * @see https://docs.geoserver.org/stable/en/user/services/wms/get_legend_graphic/index.html#get-legend-graphic
 */
@lombok.Getter
@lombok.ToString(callSuper = true)
public class WmsGetLegendGraphicRequest extends WmsRequest
{
    protected String layerName;

    public static final String LAYER_PARAMETER_NAME = "layer";
    
    protected String styleName;
    
    public static final String STYLE_PARAMETER_NAME = "style";
    
    protected int width;
    
    public static final String WIDTH_PARAMETER_NAME = "width";
    
    protected int height;
    
    public static final String HEIGHT_PARAMETER_NAME = "height";
    
    protected URI sld;
    
    public static final String SLD_PARAMETER_NAME = "sld";
    
    protected String sldBody;
    
    public static final String SLDBODY_PARAMETER_NAME = "sld_body";
    
    protected String format = OutputFormat.PNG.format;
    
    public static final String FORMAT_PARAMETER_NAME = "format";
    
    /**
     * @see https://docs.geoserver.org/stable/en/user/services/wms/get_legend_graphic/index.html#controlling-legend-appearance-with-legend-options
     */
    protected String legendOptions;
    
    public static final String LEGENDOPTIONS_PARAMETER_NAME = "legend_options";
    
    public WmsGetLegendGraphicRequest(WmsRequest.ServiceVersion version)
    {
        super(version, WmsRequest.OperationType.GET_LEGEND_GRAPHIC);
    }
    
    public WmsGetLegendGraphicRequest()
    {
        super(WmsRequest.OperationType.GET_LEGEND_GRAPHIC);
    }

    @Override
    protected void setFromMap(Map<String, String> queryParameters)
    {
        final String layerName = queryParameters.get(LAYER_PARAMETER_NAME);
        if (!StringUtils.isBlank(layerName))
            setLayerName(layerName);
        
        final String styleName = queryParameters.get(STYLE_PARAMETER_NAME);
        if (!StringUtils.isBlank(styleName))
            setStyleName(styleName);
        
        final String widthAsString = queryParameters.get(WIDTH_PARAMETER_NAME);
        if (!StringUtils.isBlank(widthAsString))
            setWidth(Integer.parseInt(widthAsString));
        
        final String heightAsString = queryParameters.get(HEIGHT_PARAMETER_NAME);
        if (!StringUtils.isBlank(heightAsString))
            setHeight(Integer.parseInt(heightAsString));
        
        final URI sld = Optional.ofNullable(queryParameters.get(SLD_PARAMETER_NAME))
            .filter(StringUtils::isNotBlank).map(URI::create).orElse(null);
        if (sld != null)
            setSld(sld);
        
        final String sldBody = queryParameters.get(SLDBODY_PARAMETER_NAME);
        if (!StringUtils.isBlank(sldBody))
            setSldBody(sldBody);
        
        final String formatAsString = queryParameters.get(FORMAT_PARAMETER_NAME);
        if (!StringUtils.isBlank(formatAsString))
            setFormat(formatAsString);
        
        final String legendOptions = queryParameters.get(LEGENDOPTIONS_PARAMETER_NAME);
        if (!StringUtils.isBlank(legendOptions))
            setLegendOptions(legendOptions);
    }
    
    void setLayerName(String layerName)
    {
        this.layerName = Objects.requireNonNull(layerName);
    }
    
    void setStyleName(String styleName)
    {
        this.styleName = Objects.requireNonNull(styleName);
    }
    
    void setWidth(int width)
    {
        this.width = width;
    }
    
    void setHeight(int height)
    {
        this.height = height;
    }
    
    void setSld(URI sld)
    {
        this.sld = Objects.requireNonNull(sld);
    }
    
    void setSldBody(String sldBody)
    {
        this.sldBody = Objects.requireNonNull(sldBody);
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
    
    void setLegendOptions(String legendOptions)
    {
        this.legendOptions = Objects.requireNonNull(legendOptions);
    }
}
