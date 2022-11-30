package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 *  @see https://portal.ogc.org/files/?artifact_id=35326 (Section 8.2)
 */
@lombok.Getter
public class WmtsGetTileRequest extends WmtsRequest
{
    /**
     * Enumeration of WMTS output (image) formats.
     */
    public enum OutputFormat
    {
        PNG("image/png"),
        
        JPEG("image/jpeg"),
        
        GEOJSON("application/json;type=geojson"),
        
        TOPOJSON("application/json;type=topojson"),
        
        UTFGRID("application/json;type=utfgrid"),
        
        MAPBOX_VECTOR_TILE("application/vnd.mapbox-vector-tile");
        
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
    
    @NotBlank
    protected String layerName;

    public static final String LAYER_PARAMETER_NAME = "Layer";
    
    protected String styleName;
    
    public static final String STYLE_PARAMETER_NAME = "Style";
    
    @NotNull
    protected OutputFormat format = OutputFormat.PNG;
    
    public static final String FORMAT_PARAMETER_NAME = "Format";
    
    @Positive
    protected int width;
    
    public static final String WIDTH_PARAMETER_NAME = "Width";
    
    @Positive
    protected int height;
    
    public static final String HEIGHT_PARAMETER_NAME = "Height";
    
    @NotBlank
    protected String tileMatrixSet;
    
    public static final String TILEMATRIXSET_PARAMETER_NAME = "TileMatrixSet";
    
    @NotBlank
    protected String tileMatrix;
    
    public static final String TILEMATRIX_PARAMETER_NAME = "TileMatrix";
    
    protected int tileRow = 0;
    
    public static final String TILEROW_PARAMETER_NAME = "TileRow";
    
    protected int tileCol = 0;
    
    public static final String TILECOL_PARAMETER_NAME = "TileCol";
    
    public WmtsGetTileRequest(ServiceVersion version)
    {
        super(version, OperationType.GET_TILE);
    }

    public WmtsGetTileRequest()
    {
        super(OperationType.GET_TILE);
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
        
        final String formatAsString = queryParameters.get(FORMAT_PARAMETER_NAME);
        if (!StringUtils.isBlank(formatAsString))
            setFormat(formatAsString);
        
        final String widthAsString = queryParameters.get(WIDTH_PARAMETER_NAME);
        if (!StringUtils.isBlank(widthAsString))
            setWidth(Integer.parseInt(widthAsString));
        
        final String heightAsString = queryParameters.get(HEIGHT_PARAMETER_NAME);
        if (!StringUtils.isBlank(heightAsString))
            setHeight(Integer.parseInt(heightAsString));
        
        final String tileMatrixSet = queryParameters.get(TILEMATRIXSET_PARAMETER_NAME);
        if (!StringUtils.isBlank(tileMatrixSet))
            setTileMatrixSet(tileMatrixSet);
        
        final String tileMatrix = queryParameters.get(TILEMATRIX_PARAMETER_NAME);
        if (!StringUtils.isBlank(tileMatrix))
            setTileMatrix(tileMatrix);
        
        final String tileRowAsString = queryParameters.get(TILEROW_PARAMETER_NAME);
        if (!StringUtils.isBlank(tileRowAsString))
            setTileRow(Integer.parseInt(tileRowAsString));
        
        final String tileColAsString = queryParameters.get(TILECOL_PARAMETER_NAME);
        if (!StringUtils.isBlank(tileColAsString))
            setTileCol(Integer.parseInt(tileColAsString));
    }
    
    void setTileMatrixSet(String tileMatrixSet)
    {
        this.tileMatrixSet = tileMatrixSet;
    }
    
    void setTileMatrix(String tileMatrix)
    {
        this.tileMatrix = tileMatrix;
    }

    void setTileRow(int tileRow)
    {
        this.tileRow = tileRow;
    }
    
    void setTileCol(int tileCol)
    {
        this.tileCol = tileCol;
    }
    
    void setFormat(String formatAsString)
    {
        final OutputFormat outputFormat = OutputFormat.fromString(formatAsString);
        Validate.isTrue(outputFormat != null, "unsupported output format: [%s]", formatAsString);
        this.format = outputFormat;
    }

    void setLayerName(String layerName)
    {
        this.layerName = layerName;
    }
    
    void setStyleName(String styleName)
    {
        this.styleName = styleName;
    }
    
    void setHeight(int height)
    {
        this.height = height;
    }

    void setWidth(int width)
    {
        this.width = width;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s.%s {version=%s, layer=%s, tileMatrix=%s, tileRow=%d, tileCol=%d, format=%s}", 
            service, request, version, layerName, tileMatrix, tileRow, tileCol, format);
    }
    
}
