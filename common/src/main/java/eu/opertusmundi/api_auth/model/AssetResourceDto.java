package eu.opertusmundi.api_auth.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class AssetResourceDto
{
    private String key;
    
    private String pid;
    
    public AssetResourceDto(String key, String pid)
    {
        this.pid = pid;
        this.key = key;
    }
    
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
    
    @Override
    public String toString()
    {
        return String.format("AssetResourceDto "
            + "{key=%s, pid=%s, created=%s, uploadedByAccountId=%s, uploadedBy=%s, fileName=%s, fileSize=%s, category=%s, fileFormat=[%s], fileEncoding=%s, crs=%s, sourceType=%s, parentKey=%s, path=%s}",
            key, pid, created, uploadedByAccountId, uploadedBy, fileName, fileSize, category, fileFormat, fileEncoding,
            crs, sourceType, parentKey, path);
    }
}
