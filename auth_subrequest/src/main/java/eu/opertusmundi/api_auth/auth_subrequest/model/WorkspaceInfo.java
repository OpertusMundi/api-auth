package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Objects;
import java.util.UUID;

@lombok.Getter
public class WorkspaceInfo
{
    final WorkspaceType type;
    
    final String providerAccountKey;
    
    public WorkspaceInfo(WorkspaceType type, String providerAccountKey)
    {
        this.type = Objects.requireNonNull(type);
        this.providerAccountKey = Objects.requireNonNull(providerAccountKey);
    }

    public static WorkspaceInfo forPrivateWorkspace(UUID providerAccountKey)
    {
        return new WorkspaceInfo(WorkspaceType.PRIVATE, providerAccountKey.toString());
    }
    
    public static WorkspaceInfo forPublicWorkspace(UUID providerAccountKey)
    {
        return new WorkspaceInfo(WorkspaceType.PUBLIC, providerAccountKey.toString());
    }
    
    @Override
    public String toString()
    {
        return String.format("{type=%s, providerAccountKey=%s}", type, providerAccountKey);
    }
}
