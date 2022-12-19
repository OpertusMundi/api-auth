package eu.opertusmundi.api_auth.model;

import java.util.Objects;
import java.util.UUID;

@lombok.Getter
public class WorkspaceInfo
{
    final WorkspaceType type;
    
    final UUID providerAccountKey;
    
    WorkspaceInfo(WorkspaceType type, UUID providerAccountKey)
    {
        this.type = Objects.requireNonNull(type);
        this.providerAccountKey = Objects.requireNonNull(providerAccountKey);
    }

    public static WorkspaceInfo of(WorkspaceType type, String providerAccountKeyAsString)
    {
        return new WorkspaceInfo(type, UUID.fromString(providerAccountKeyAsString));
    }
    
    public static WorkspaceInfo of(WorkspaceType type, UUID providerAccountKey)
    {
        return new WorkspaceInfo(type, providerAccountKey);
    }
    
    public static WorkspaceInfo forPrivateWorkspace(UUID providerAccountKey)
    {
        return new WorkspaceInfo(WorkspaceType.PRIVATE, providerAccountKey);
    }
    
    public static WorkspaceInfo forPublicWorkspace(UUID providerAccountKey)
    {
        return new WorkspaceInfo(WorkspaceType.PUBLIC, providerAccountKey);
    }
    
    @Override
    public String toString()
    {
        return String.format("{type=%s, providerAccountKey=%s}", type, providerAccountKey);
    }
}
