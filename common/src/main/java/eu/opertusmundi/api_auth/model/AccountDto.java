package eu.opertusmundi.api_auth.model;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@lombok.Getter
@lombok.Setter
@JsonInclude(Include.NON_EMPTY)
public class AccountDto
{
    private Integer id;

    private UUID key;
    
    private AccountDto parent;
    
    private boolean blocked;
    
    private boolean active;

    private String email;

    private boolean emailVerified;
    
    private List<AccountClientDto> clients;

    @Override
    public String toString()
    {
        return String.format("AccountDto {id=%s, key=%s, blocked=%s, active=%s, email=%s}", 
            id, key, blocked, active, email);
    }
    
    
}
