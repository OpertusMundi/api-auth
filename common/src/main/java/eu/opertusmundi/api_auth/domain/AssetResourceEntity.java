package eu.opertusmundi.api_auth.domain;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import eu.opertusmundi.api_auth.model.AssetResourceDto;
import eu.opertusmundi.api_auth.model.AssetType;
import eu.opertusmundi.api_auth.model.ResourseSourceType;

@lombok.Setter
@lombok.Getter
@Entity(name = "AssetResource")
@Immutable
@Table(schema = "file", name = "`asset_published_resource_view`")
public class AssetResourceEntity
{
    /**
     * The persistent identifier (pid) for an asset
     */
    @Id
    @Column(name = "`pid`")
    private String pid;
    
    @NotNull
    @Column(name = "`key`", updatable = false, unique = true)
    private String key;

    @NotNull
    @Column(name = "`created_on`", nullable = false)
    private ZonedDateTime created;
    
    /**
     * The owner of the asset (the provider or a child of the provider account)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`uploaded_by`", nullable = false)
    private AccountEntity uploadedBy;

    /**
     * The original file name
     */
    @NotNull
    @Column(name = "`file_name`", nullable = false)
    private String fileName;
    
    /**
     * The size of the original file
     */
    @NotNull
    @Column(name = "`size`")
    private Long fileSize;
    
    @Column(name = "`category`")
    @Enumerated(EnumType.STRING)
    private AssetType category;

    /**
     * The file format of the original file
     */
    @NotNull
    @Column(name = "`format`", nullable = false)
    private String fileFormat;

    /**
     * The character encoding of the original file (e.g. 'UTF-8')
     */
    @Column(name = "`encoding`")
    private String fileEncoding;

    /**
     * The CRS (Coordinate Reference System) used in the original file
     */
    @Column(name = "`crs`")
    private String crs;
    
    @Column(name = "`source`")
    @Enumerated(EnumType.STRING)
    private ResourseSourceType sourceType;

    // FIXME parent_id column name is misleading (it points to `key`)
    @Column(name = "`parent_id`", nullable = true)
    private String parentKey;

    /**
     * The (relative) path where the uploaded file resides on user's home
     */
    @Column(name = "`path`")
    private String path;
    
    public AssetResourceDto toDto()
    {
        return toDto(false);
    }
    
    public AssetResourceDto toDto(boolean convertAccountToDto)
    {
        final AssetResourceDto d = new AssetResourceDto(key, pid);
        
        d.setCreated(created);
        
        if (uploadedBy != null) {
            d.setUploadedByAccountId(uploadedBy.getId());
            if (convertAccountToDto) 
                d.setUploadedBy(uploadedBy.toDto(false, false, false));
        }
        
        d.setFileName(fileName);
        d.setFileSize(fileSize);
        d.setCategory(category);
        d.setFileFormat(fileFormat);
        d.setFileEncoding(fileEncoding);
        d.setCrs(crs);
        d.setSourceType(sourceType);
        d.setPath(path);
        
        d.setParentKey(parentKey);
        
        return d;
    }
    
}