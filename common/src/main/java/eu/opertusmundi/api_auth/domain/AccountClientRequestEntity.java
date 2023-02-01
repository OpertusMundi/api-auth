package eu.opertusmundi.api_auth.domain;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import eu.opertusmundi.api_auth.domain.usertype.StringArrayUserType;
import eu.opertusmundi.api_auth.model.AccountClientRequestDto;
import eu.opertusmundi.api_auth.model.OwsServiceInfo;
import eu.opertusmundi.api_auth.model.WorkspaceInfo;

@lombok.Getter
@lombok.Setter
@TypeDefs({
    @TypeDef(name = "string-array", typeClass = StringArrayUserType.class),
})
@Entity(name = "AccountClientRequest")
@Table(schema = "web", name = "`account_client_request`")
public class AccountClientRequestEntity
{
    protected AccountClientRequestEntity() {}

    @Id
    @Column(name = "`request_id`", updatable = false, nullable = false, columnDefinition = "char(32)")
    private String requestId;
    
    @NotNull
    @Column(name = "`recorded`", updatable = false, nullable = false)
    private ZonedDateTime recorded;
    
    @NotNull
    @Column(name = "`client_id`", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID clientKey;
    
    @NotNull
    @Column(name = "`virtual_host`", updatable = false, nullable = false)
    private String hostname;
    
    @NotNull
    @Column(name = "`uri`", updatable = false)
    private String uri;
    
    @Embedded
    private WorkspaceInfoEmbeddable workspaceInfo;
    
    @Embedded
    private OwsServiceInfoEmbeddable owsServiceInfo;
    
    @Type(type = "string-array")
    @Column(name = "`asset_keys`", columnDefinition = "text[]", updatable = false)
    private String[] assetKeys; 

    public AccountClientRequestEntity(AccountClientRequestDto dto) 
    {
        this.recorded = dto.getRecorded();
        this.requestId = dto.getRequestId();
        this.clientKey = dto.getClientKey();
        this.hostname = dto.getHostname();
        this.uri = Optional.ofNullable(dto.getUri())
            .map(URI::toString).orElse(null);
        this.workspaceInfo = Optional.ofNullable(dto.getWorkspaceInfo())
            .map(WorkspaceInfoEmbeddable::new).orElse(null);
        this.owsServiceInfo = Optional.ofNullable(dto.getOwsServiceInfo())
            .map(OwsServiceInfoEmbeddable::new).orElse(null);
        this.assetKeys = dto.getAssetKeys();
    }
    
    public AccountClientRequestDto toDto()
    {
        var b = AccountClientRequestDto.builder()
            .recorded(recorded)
            .requestId(requestId)
            .clientKey(clientKey)
            .hostname(hostname)
            .uri(URI.create(uri));
        
        if (workspaceInfo != null) {
            b.workspaceInfo(WorkspaceInfo.of(
                workspaceInfo.type, workspaceInfo.providerAccountKey));
        }
        
        if (owsServiceInfo != null) {
            b.owsServiceInfo(OwsServiceInfo.of(
                owsServiceInfo.serviceType, owsServiceInfo.serviceVersion, owsServiceInfo.request));
        }
        
        if (assetKeys != null) {
            b.assetKeys(assetKeys);
        }
        
        return b.build();
    }
}
