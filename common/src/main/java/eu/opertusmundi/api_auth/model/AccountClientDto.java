package eu.opertusmundi.api_auth.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AccountClientDto extends ClientDto
{
    public AccountClientDto(String clientId)
    {
        super(clientId);
    }
    
    public AccountClientDto(UUID clientId)
    {
        super(clientId == null? null : clientId.toString());
    }
    
    private AccountDto account;
    
    public AccountClientDto withAccount(AccountDto account) 
    {
        this.account = account;
        return this;
    }
    
    private String alias;
    
    private ZonedDateTime createdOn;

    private ZonedDateTime revokedOn;
}
