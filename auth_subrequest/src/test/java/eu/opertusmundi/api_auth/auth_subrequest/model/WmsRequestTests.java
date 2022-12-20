package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import eu.opertusmundi.api_auth.auth_subrequest.model.WmsDescribeLayerRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetCapabilitiesRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsGetMapRequest;
import eu.opertusmundi.api_auth.auth_subrequest.model.WmsRequest;

import static eu.opertusmundi.api_auth.auth_subrequest.util.QueryStringUtil.*;
import static eu.opertusmundi.api_auth.model.OwsServiceType.WMS;

public class WmsRequestTests
{
    @Test
    public void buildInvalidRequest_1()
    {
        final String uriAsString = "/work_1/ows?service=wms&version=1.1.1&request=MakeCoffee";
        final URI uri = URI.create(uriAsString);
        final Map<String, String> queryParameters = parseQueryStringToMap(uri);
        final IllegalArgumentException ex = 
            assertThrows(IllegalArgumentException.class, () -> WmsRequest.fromMap(queryParameters));
        assertTrue(ex.getMessage().indexOf("request") >= 0);
    }
    
    @Test
    public void buildGetCapabilitiesRequest()
    {
        final String uriAsString = "/work_1/ows?service=wms&version=1.1.1&request=GetCapabilities";
        final URI uri = URI.create(uriAsString);
        //System.err.println("uri.path=" + uri.getPath());
        //System.err.println("uri.query=" + uri.getQuery());
        final Map<String, String> queryParameters = parseQueryStringToMap(uri);
        final WmsGetCapabilitiesRequest request = (WmsGetCapabilitiesRequest) WmsRequest.fromMap(queryParameters);
        
        assertEquals(WMS, request.getService());
        assertEquals("1.1.1", request.getVersion());
        assertEquals("GetCapabilities", request.getRequest());
    }
    
    @Test
    public void buildDescribeLayerRequest_1()
    {
        final String uriAsString = "/work_1/wms?version=1.1.1" +
            "&request=DescribeLayer" +
            "&layers=_85a5a485-61b8-4189-b4af-bc4a2b01241a,_10237874-b95e-4152-8fd4-08ee5308c750" +
            "&output_format=text/xml";
        final URI uri = URI.create(uriAsString);
        //System.err.println("uri.path=" + uri.getPath());
        //System.err.println("uri.query=" + uri.getQuery());
        final Map<String, String> queryParameters = parseQueryStringToMap(uri);
        final WmsDescribeLayerRequest request = (WmsDescribeLayerRequest) WmsRequest.fromMap(queryParameters);
        
        assertEquals(WMS, request.getService());
        assertEquals("1.1.1", request.getVersion());
        assertEquals("DescribeLayer", request.getRequest());
        assertEquals("text/xml", request.getOutputFormat().toFormatString());
        assertEquals(List.of("_85a5a485-61b8-4189-b4af-bc4a2b01241a", "_10237874-b95e-4152-8fd4-08ee5308c750"), request.getLayerNames());
    }
    
    @Test
    public void buildDescribeLayerRequest_2()
    {
        final String uriAsString = "/work_1/wms?version=1.1.1" +
            "&request=DescribeLayer" +
            "&layers=_85a5a485-61b8-4189-b4af-bc4a2b01241a,_10237874-b95e-4152-8fd4-08ee5308c750" +
            "&output_format=text/html"; // unsupported output format
        final URI uri = URI.create(uriAsString);
        final Map<String, String> queryParameters = parseQueryStringToMap(uri);
        final IllegalArgumentException ex = 
            assertThrows(IllegalArgumentException.class, () -> WmsRequest.fromMap(queryParameters));
        assertTrue(ex.getMessage().indexOf("output_format") >= 0, "expected a reference to problematic field");
    }
    
    @Test
    public void buildGetMapRequest_simple()
    {
        final String uriAsString = "/work_1/wms?service=WMS" +
            "&version=1.1.1" +
            "&request=GetMap" +
            "&layers=_159d482d-3b44-45d3-8052-6d7dc2085521" +
            "&styles=" +
            "&format=image%2Fpng" +
            "&transparent=true" +
            "&width=256" +
            "&height=256" +
            "&srs=EPSG%3A3857" +
            "&bbox=2646555.667345943,4569099.802774696,2651447.637156194,4573991.772584948";
        final URI uri = URI.create(uriAsString);
        final Map<String, String> queryParameters = parseQueryStringToMap(uri);
        final WmsGetMapRequest request = (WmsGetMapRequest) WmsRequest.fromMap(queryParameters);
        
        assertEquals(WmsRequest.ServiceVersion.V_1_1_1.versionAsString(), request.getVersion());
        assertEquals(List.of("_159d482d-3b44-45d3-8052-6d7dc2085521"), request.getLayerNames());
        assertEquals("image/png", request.getFormat().toFormatString());
        assertEquals(256, request.getHeight());
        assertEquals(256, request.getWidth());
        assertEquals("EPSG:3857", request.getSrs());
        assertNull(request.getBgcolor(), "No BGCOLOR expected");
        assertEquals(true, request.getTransparent());
        assertThat(request.getBbox(), allOf(
            hasProperty("minx", closeTo(2646555.667345943, 1E-6)),
            hasProperty("miny", closeTo(4569099.802774696, 1E-6)),
            hasProperty("maxx", closeTo(2651447.637156194, 1E-6)),
            hasProperty("maxy", closeTo(4573991.772584948, 1E-6))));
        assertNull(request.getSld(), "No SLD expected");
        assertNull(request.getSldBody(), "No SLD_BODY expected");
    }
    
    @Test
    public void buildGetMapRequest_withSld()
    {
        final String uriAsString = "/work_1/wms?service=WMS" +
            "&version=1.1.1" +
            "&request=GetMap" +
            "&layers=_159d482d-3b44-45d3-8052-6d7dc2085521" +
            "&styles=" +
            "&format=image%2Fpng" +
            "&transparent=true" +
            "&width=256" +
            "&height=256" +
            "&srs=EPSG%3A3857" +
            "&bbox=2646555.667345943,4569099.802774696,2651447.637156194,4573991.772584948" +
            "&sld=http%3A%2F%2Fexample.com%2Fstyles%2F1.sld";
        final URI uri = URI.create(uriAsString);
        final Map<String, String> queryParameters = parseQueryStringToMap(uri);
        final WmsGetMapRequest request = (WmsGetMapRequest) WmsRequest.fromMap(queryParameters);
       
        assertEquals(request.getSld(), URI.create("http://example.com/styles/1.sld"));
        assertNull(request.getSldBody(), "No SLD_BODY expected");
    }
}
