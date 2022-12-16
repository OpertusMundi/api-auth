package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.net.URI;
import java.util.Collections;
import java.util.List;
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
    
    protected OutputFormat format = OutputFormat.PNG;
    
    public static final String FORMAT_PARAMETER_NAME = "format";
    
    /**
     * @see https://docs.geoserver.org/stable/en/user/services/wms/get_legend_graphic/index.html#controlling-legend-appearance-with-legend-options
     */
    protected String legendOptions;
    
    public static final String LEGENDOPTIONS_PARAMETER_NAME = "legend_options";
    
    public WmsGetLegendGraphicRequest(ServiceVersion version)
    {
        super(version, OperationType.GET_LEGEND_GRAPHIC);
    }
    
    public WmsGetLegendGraphicRequest()
    {
        super(OperationType.GET_LEGEND_GRAPHIC);
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
    
    public void setLayerName(String layerName)
    {
        this.layerName = Objects.requireNonNull(layerName);
    }
    
    public void setStyleName(String styleName)
    {
        this.styleName = Objects.requireNonNull(styleName);
    }
    
    public void setWidth(int width)
    {
        this.width = width;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }
    
    public void setSld(URI sld)
    {
        this.sld = Objects.requireNonNull(sld);
    }
    
    public void setSldBody(String sldBody)
    {
        this.sldBody = Objects.requireNonNull(sldBody);
    }
    
    public void setFormat(OutputFormat f)
    {
        this.format = Objects.requireNonNull(f);
    }
    
    public void setFormat(String formatAsString)
    {
        final OutputFormat outputFormat = OutputFormat.fromString(Objects.requireNonNull(formatAsString));
        Validate.isTrue(outputFormat != null, "unsupported output format: [%s]", formatAsString);
        this.format = outputFormat;
    }
    
    public void setLegendOptions(String legendOptions)
    {
        this.legendOptions = Objects.requireNonNull(legendOptions);
    }
    
    @Override
    public String toString()
    {
        return String.format("%s.%s {version=%s, layer=%s}", 
            service, request, version, layerName);
    }
    
    @Override
    public List<String> getLayerNames()
    {
        return Collections.singletonList(layerName);
    }
}
