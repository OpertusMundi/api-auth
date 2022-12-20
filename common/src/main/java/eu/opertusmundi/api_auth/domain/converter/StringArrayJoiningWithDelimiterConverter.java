package eu.opertusmundi.api_auth.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringArrayJoiningWithDelimiterConverter implements AttributeConverter<String[], String>
{
    public static final String DELIMITER = ",";
    
    @Override
    public String convertToDatabaseColumn(String[] value)
    {
        return value == null || value.length == 0? null : String.join(DELIMITER, value);
    }

    @Override
    public String[] convertToEntityAttribute(String data)
    {
        return data == null || data.isEmpty()? null : data.split(DELIMITER);
    }
}
