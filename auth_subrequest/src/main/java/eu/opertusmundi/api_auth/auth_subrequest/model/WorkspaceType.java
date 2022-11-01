package eu.opertusmundi.api_auth.auth_subrequest.model;

import org.apache.commons.lang3.StringUtils;

public enum WorkspaceType
{
    /**
     * A type of workspace for public-facing (commercial) APIs
     */
    PUBLIC("X"),
    
    /**
     * A type of workspace for private (under provider's organization) APIs
     */
    PRIVATE("P"),
    ;
    
    @lombok.Getter
    private final String prefix;
    
    private WorkspaceType(String prefix)
    {
        this.prefix = prefix;
    }
    
    public static WorkspaceType fromPrefix(String prefix) 
    {
        if (!StringUtils.isBlank(prefix)) {
            for (WorkspaceType t: WorkspaceType.values())
                if (t.prefix.equalsIgnoreCase(prefix))
                    return t;
        }
        return PUBLIC; // the default type  
    }
}