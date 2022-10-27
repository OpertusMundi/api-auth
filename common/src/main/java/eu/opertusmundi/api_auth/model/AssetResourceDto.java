package eu.opertusmundi.api_auth.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.ToString
@JsonInclude(Include.NON_EMPTY)
public class AssetResourceDto
{
    private Integer id;

    private String key;
    
    public AssetResourceDto(Integer id, String key)
    {
        this.id = id;
        this.key = key;
    }
    
    private String pid;
    
    private ZonedDateTime created;
    
    /**
     * a reference to the owner account (if respective account DTO is missing)
     */
    private Integer uploadedByAccountId;
    
    private AccountDto uploadedBy;
    
    private String fileName;
    
    private Long fileSize;
    
    private AssetType category;

    private String fileFormat;

    private String fileEncoding;

    private String crs;
    
    private ResourseSourceType sourceType;
    
    private String parentKey;
    
    private String path;
}
