package eu.opertusmundi.api_auth.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import eu.opertusmundi.api_auth.model.AccountClientDto;

@lombok.Getter
@lombok.Setter
@Entity(name = "AccountClient")
@Immutable
@Table(schema = "web", name = "`account_client`")
public class AccountClientEntity
{
    protected AccountClientEntity() {}

    @Id
    @Column(name = "`id`", updatable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`account`", nullable = false)
    private AccountEntity account;

    @NotNull
    @Column(name = "`alias`")
    private String alias;

    @NotNull
    @Column(name = "client_id", updatable = false, columnDefinition = "uuid", unique = true)
    private UUID key;

    @NotNull
    @Column(name = "`created_on`")
    private ZonedDateTime created;

    @Column(name = "`revoked_on`")
    private ZonedDateTime revoked;
    
    public AccountClientDto toDto(boolean convertAccountToDto)
    {
        final AccountClientDto d = new AccountClientDto(key);
        
        d.setAlias(alias);
        d.setCreated(created);
        d.setRevoked(revoked);
        
        if (account != null) {
            d.setAccountId(account.getId());
            if (convertAccountToDto) 
                d.setAccount(account.toDto(false, false, false));
        }
        
        return d;
    }
    
    public AccountClientDto toDto() 
    {
        return toDto(false);
    }
}
