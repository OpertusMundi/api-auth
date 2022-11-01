package eu.opertusmundi.api_auth.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import eu.opertusmundi.api_auth.model.AccountSubscriptionDto;
import eu.opertusmundi.api_auth.model.AssetSourceType;
import eu.opertusmundi.api_auth.model.SubscriptionStatus;
import eu.opertusmundi.api_auth.model.TopicCategory;

@lombok.Setter
@lombok.Getter
@Entity(name = "AccountSubscription")
@Immutable
@Table(schema = "web", name = "`account_active_subscription_view`")
public class AccountSubscriptionEntity
{
    @Id
    @Column(name = "`id`", updatable = false)
    private Integer id;

    @NotNull
    @Column(name = "`key`", updatable = false, columnDefinition = "uuid", unique = true)
    private UUID key;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`consumer`", nullable = false)
    private AccountEntity consumer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`provider`", nullable = false)
    private AccountEntity provider;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "`asset`", nullable = false)
    private AssetResourceEntity asset;
    
    @NotNull
    @Column(name = "`added_on`", nullable = false)
    private ZonedDateTime added;

    @NotNull
    @Column(name = "`updated_on`", nullable = false)
    private ZonedDateTime updated;

    @Column(name = "`expires_on`")
    private ZonedDateTime expires;

    @Column(name = "`cancelled_on`")
    private ZonedDateTime cancelled;
    
    @NotNull
    @Column(name = "`source`", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssetSourceType source;
    
    @Column(name = "`segment`")
    @Enumerated(EnumType.STRING)
    private TopicCategory topic;

    @NotNull
    @Column(name = "`status`", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
        
    public AccountSubscriptionDto toDto()
    {
        return toDto(false);
    }
    
    public AccountSubscriptionDto toDto(boolean convertAccountToDto)
    {
        final AccountSubscriptionDto d = new AccountSubscriptionDto(id, key);

        if (consumer != null) {
            d.setConsumerAccountId(consumer.getId());
            if (convertAccountToDto)
                d.setConsumer(consumer.toDto(false, false, false));
        }
        
        if (provider != null) {
            d.setProviderAccountId(provider.getId());
            if (convertAccountToDto)
                d.setProvider(provider.toDto(false, false, false));
        }
        
        d.setAsset(asset.toDto(false));
        
        d.setAdded(added);
        d.setUpdated(updated);
        d.setCancelled(cancelled);
        d.setExpires(expires);
        
        d.setSource(source);
        d.setTopic(topic);
        d.setStatus(status);
        
        return d;
    }
}
