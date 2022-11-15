package eu.opertusmundi.api_auth.auth_subrequest.model.exception;

import java.util.UUID;

import eu.opertusmundi.api_auth.auth_subrequest.model.WorkspaceInfo;

public class ConsumerNotAuthorizedForWorkspaceException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    @lombok.Getter
    private final UUID consumerAccountKey;
    
    @lombok.Getter
    private final WorkspaceInfo workspaceInfo;
    
    public ConsumerNotAuthorizedForWorkspaceException(UUID consumerAccountKey, WorkspaceInfo workspaceInfo)
    {
        super(String.format("consumer [%s] is not authorized to access workspace %s", 
            consumerAccountKey, workspaceInfo));
        this.consumerAccountKey = consumerAccountKey;
        this.workspaceInfo = workspaceInfo;
    }
}
