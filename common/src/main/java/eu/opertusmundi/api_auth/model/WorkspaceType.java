package eu.opertusmundi.api_auth.model;

public enum WorkspaceType
{
    /**
     * A type of workspace for public-facing (commercial, subscription-based) APIs
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
        if (prefix != null && !prefix.isBlank()) {
            for (WorkspaceType t: WorkspaceType.values())
                if (t.prefix.equalsIgnoreCase(prefix))
                    return t;
        }
        return PUBLIC; // the default type  
    }
}