package eu.opertusmundi.api_auth.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@JsonInclude(Include.NON_EMPTY)
public class AccountSubscriptionDto
{
    private Integer id;

    private UUID key;
    
    private AccountDto consumer;
    
    private AccountDto provider;
    
    private String asset;
    
    private ZonedDateTime added;

    private ZonedDateTime updated;

    private ZonedDateTime expires;

    private ZonedDateTime cancelled;
    
    private AssetSourceType source;
    
    private TopicCategory topic;

    private SubscriptionStatus status;
}
