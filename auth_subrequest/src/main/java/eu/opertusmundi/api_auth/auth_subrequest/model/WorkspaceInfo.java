package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.Objects;

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

    @Override
    public String toString()
    {
        return String.format("{type=%s, providerAccountKey=%s}", type, providerAccountKey);
    }
}
