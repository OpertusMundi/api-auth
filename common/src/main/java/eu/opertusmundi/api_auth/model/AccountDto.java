package eu.opertusmundi.api_auth.model;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@lombok.Getter
@lombok.Setter
@JsonInclude(Include.NON_EMPTY)
public class AccountDto
{
    @NotNull
    @Positive
    private Integer id;

    @NotNull
    private UUID key;
    
    @Positive
    private Integer parentId;
    
    private AccountDto parent;
    
    private boolean blocked;
    
    private boolean active;

    @NotNull
    @Email
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
