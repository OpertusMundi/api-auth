package eu.opertusmundi.api_auth.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@lombok.Getter
@lombok.NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AccountClientDto extends ClientDto
{    
    public AccountClientDto(UUID key)
    {
        super(key == null? null : key.toString());
        this.keyAsUuid = key;
    }
    
    @NotNull
    private UUID keyAsUuid;
    
    @lombok.Setter
    @NotNull 
    @Positive
    private Integer accountId;
    
    @lombok.Setter
    @NotNull
    private AccountDto account;
    
    public AccountClientDto withAccount(AccountDto account) 
    {
        this.account = account;
        return this;
    }
    
    @lombok.Setter
    @NotBlank
    private String alias;
    
    @lombok.Setter
    private ZonedDateTime created;

    @lombok.Setter
    private ZonedDateTime revoked;

    @Override
    public String toString()
    {
        return String.format(
            "AccountClientDto {key=%s, accountId=%s, account=%s, alias=%s}",
            key, accountId, account, alias);
    }
}
