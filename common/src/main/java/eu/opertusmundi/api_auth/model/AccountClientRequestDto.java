package eu.opertusmundi.api_auth.model;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@lombok.Getter
@lombok.Setter
@lombok.RequiredArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@JsonInclude(Include.NON_NULL)
public class AccountClientRequestDto
{
    private final String requestId;
    
    private final ZonedDateTime recorded;
    
    private final UUID clientKey;
    
    private String hostname;
    
    private URI uri;
    
    private WorkspaceInfo workspaceInfo;
    
    private OwsServiceInfo owsServiceInfo;
    
    private String[] assetKeys;

    @Override
    public String toString()
    {
        return String.format(
            "AccountClientRequestDto ["
                + "requestId=%s, recorded=%s, clientKey=%s, hostname=%s, uri=%s, workspaceInfo=%s, "
                + "owsServiceInfo=%s, assetKeys=%s]",
            requestId, recorded, clientKey, hostname, uri, workspaceInfo, 
            owsServiceInfo, Arrays.toString(assetKeys));
    }
}
