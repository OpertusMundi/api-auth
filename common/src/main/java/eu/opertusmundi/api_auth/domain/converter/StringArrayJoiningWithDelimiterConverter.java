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
        return value == null? null : String.join(DELIMITER, value);
    }

    @Override
    public String[] convertToEntityAttribute(String data)
    {
        return data == null? null : data.split(DELIMITER);
    }
}
