package eu.opertusmundi.api_auth.auth_subrequest.model;

import java.util.UUID;

public class ConsumerNotAuthorizedForWorkspace extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    @lombok.Getter
    private final UUID consumerAccountKey;
    
    @lombok.Getter
    private final WorkspaceInfo workspaceInfo;
    
    public ConsumerNotAuthorizedForWorkspace(UUID consumerAccountKey, WorkspaceInfo workspaceInfo)
    {
        super(String.format("consumer [%s] is not authorized to access workspace %s", 
            consumerAccountKey, workspaceInfo));
        this.consumerAccountKey = consumerAccountKey;
        this.workspaceInfo = workspaceInfo;
    }
}
