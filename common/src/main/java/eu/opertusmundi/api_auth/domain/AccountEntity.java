package eu.opertusmundi.api_auth.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NaturalId;

import eu.opertusmundi.api_auth.model.AccountClientDto;
import eu.opertusmundi.api_auth.model.AccountDto;

@lombok.Setter
@lombok.Getter
@Entity(name = "Account")
@Immutable
@Table(schema = "web", name = "`account`")
public class AccountEntity 
{
    @Id
    @Column(name = "`id`", updatable = false)
    private Integer id;

    @NotNull
    @NaturalId
    @Column(name = "key", updatable = false, columnDefinition = "uuid")
    private UUID key;
    
    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "`parent`")
    private AccountEntity parent;
    
    @Column(name = "`active`")
    private boolean active;
    
    @Column(name = "`blocked`")
    private boolean blocked;

    @NotNull
    @Email
    @Column(name = "`email`", nullable = false)
    private String email;

    @Column(name = "`email_verified`")
    private boolean emailVerified;
    
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountClientEntity> clients = new ArrayList<>();

    public AccountDto toDto()
    {
        return this.toDto(false, false, false);
    }
    
    public AccountDto toDto(boolean includeParent, boolean includeClients, boolean backLink)
    {
        final AccountDto d = new AccountDto();
        
        d.setId(id);
        d.setKey(key);
        
        if (includeParent && parent != null) {
            d.setParent(parent.toDto(false, false, false));
        }
        
        d.setActive(active);
        d.setBlocked(blocked);
        
        d.setEmail(email);
        d.setEmailVerified(emailVerified);
        
        if (includeClients) {
            Stream<AccountClientDto> clientDtoStream = clients.stream()
                .map(AccountClientEntity::toDto);
            if (backLink) {
                clientDtoStream = clientDtoStream.map(c -> c.withAccount(d));
            }
            d.setClients(clientDtoStream.collect(Collectors.toUnmodifiableList()));
        }
        
        return d;
    }
}
