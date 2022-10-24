package eu.opertusmundi.api_auth.auth_subrequest.util;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    
    static final Function<String, String> toLowerCaseConverter = String::toLowerCase;
    
    private static final BinaryOperator<String> getRight = (x, y) -> y;
    
    public static Map<String, String> parseQueryStringToMap(String query)
    {
        if (StringUtils.isBlank(query))
            return Collections.emptyMap();
        return Arrays.stream(StringUtils.split(query, '&')).map(nameValueParser)
            .collect(Collectors.toUnmodifiableMap(toLowerCaseConverter.compose(Pair::getKey), 
                urlDecoder.compose(Pair::getValue), getRight));
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
        return Arrays.stream(StringUtils.split(query, '&')).map(nameValueParser)
            .collect(Collectors.groupingBy(toLowerCaseConverter.compose(Pair::getKey), 
                Collectors.mapping(urlDecoder.compose(Pair::getValue), Collectors.toUnmodifiableList())));
    }
    
    public static Map<String, List<String>> parseQueryStringToMultimap(URI uri)
    {
        Validate.notNull(uri, "uri must be non null");
        return parseQueryStringToMultimap(uri.getRawQuery());
    }
}
