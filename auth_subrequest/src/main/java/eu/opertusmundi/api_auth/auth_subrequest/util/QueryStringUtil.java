package eu.opertusmundi.api_auth.auth_subrequest.util;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class QueryStringUtil
{
    static final Function<String, Pair<String, String>> nameValueParser = (String s) -> 
    {
        final int splitIndex = s.indexOf('=');
        return splitIndex < 0? Pair.of(s, "") :
            Pair.of(s.substring(0, splitIndex), s.substring(splitIndex + 1));
    };
    
    static final Charset defaultCharset = Charset.defaultCharset();
    
    static final Function<String, String> urlDecoder = s -> URLDecoder.decode(s, defaultCharset);
    
    private static final BinaryOperator<String> mergeIgnoringLeft = (x, y) -> y;
    
    public static Map<String, String> parseQueryStringToMap(String query)
    {
        if (StringUtils.isBlank(query))
            return Collections.emptyMap();
        
        final Map<String, String> map = Arrays.stream(StringUtils.split(query, '&')).map(nameValueParser)
            .collect(Collectors.toMap(Pair::getKey, urlDecoder.compose(Pair::getValue), 
                mergeIgnoringLeft, CaseInsensitiveMap::new));
        
        return Collections.unmodifiableMap(map);
    }
    
    public static Map<String, String> parseQueryStringToMap(URI uri)
    {
        Validate.notNull(uri, "uri must be non null");
        return parseQueryStringToMap(uri.getRawQuery());
    }
    
    public static Map<String, List<String>> parseQueryStringToMultimap(String query)
    {
        if (StringUtils.isBlank(query))
            return Collections.emptyMap();
        
        final Map<String, List<String>> map = Arrays.stream(StringUtils.split(query, '&')).map(nameValueParser)
            .collect(Collectors.groupingBy(Pair::getKey, CaseInsensitiveMap::new,
                Collectors.mapping(urlDecoder.compose(Pair::getValue), Collectors.toUnmodifiableList())));
        
        return Collections.unmodifiableMap(map);
    }
    
    public static Map<String, List<String>> parseQueryStringToMultimap(URI uri)
    {
        Validate.notNull(uri, "uri must be non null");
        return parseQueryStringToMultimap(uri.getRawQuery());
    }
}
