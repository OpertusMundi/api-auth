package eu.opertusmundi.api_auth.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import eu.opertusmundi.api_auth.model.WorkspaceInfo;
import eu.opertusmundi.api_auth.model.WorkspaceType;

@Embeddable
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
class WorkspaceInfoEmbeddable
{
    @Enumerated(EnumType.STRING)
    @Column(name = "`workspace_type`", columnDefinition = "provider_workspace_type_t")
    WorkspaceType type;
    
    @Column(name = "`workspace_provider_key`", columnDefinition = "uuid")
    UUID providerAccountKey;
    
    public WorkspaceInfoEmbeddable(WorkspaceInfo workspaceInfo)
    {
        this.type = workspaceInfo.getType();
        this.providerAccountKey = workspaceInfo.getProviderAccountKey();
    }
}
