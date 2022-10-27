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
    public AccountClientDto(String key)
    {
        super(key);
    }
    
    public AccountClientDto(UUID key)
    {
        super(key == null? null : key.toString());
    }
    
    private Integer accountId;
    
    private AccountDto account;
    
    public AccountClientDto withAccount(AccountDto account) 
    {
        this.account = account;
        return this;
    }
    
    private String alias;
    
    private ZonedDateTime created;

    private ZonedDateTime revoked;

    @Override
    public String toString()
    {
        return String.format(
            "AccountClientDto {key=%s, accountId=%s, account=%s, alias=%s}",
            key, accountId, account, alias);
    }
}
