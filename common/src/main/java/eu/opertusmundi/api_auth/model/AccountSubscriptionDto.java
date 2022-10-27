package eu.opertusmundi.api_auth.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class AccountSubscriptionDto
{
    private Integer id;

    private UUID key;
    
    public AccountSubscriptionDto(Integer id, UUID key)
    {
        this.id = id;
        this.key = key;
    }
    
    private AccountDto consumer;
    
    /** 
     * a reference to the consumer account (if consumer account DTO is missing) 
     */
    private Integer consumerAccountId; 
    
    private AccountDto provider;
    
    /** 
     * a reference to the provider account (if provider account DTO is missing) 
     */
    private Integer providerAccountId;
    
    /**
     * The persistent-identifier (pid) of the asset
     */
    private String asset;
    
    private ZonedDateTime added;

    private ZonedDateTime updated;

    private ZonedDateTime expires;

    private ZonedDateTime cancelled;
    
    private AssetSourceType source;
    
    private TopicCategory topic;

    private SubscriptionStatus status;

    @Override
    public String toString()
    {
        return String.format(
            "AccountSubscriptionDto {id=%s, key=%s, consumer=%s, consumerAccountId=%s, provider=%s, providerAccountId=%s, asset=%s, added=%s, updated=%s, expires=%s, cancelled=%s, source=%s, topic=%s, status=%s}",
            id, key, consumer, consumerAccountId, provider, providerAccountId, asset, added, updated, expires,
            cancelled, source, topic, status);
    }    
}
