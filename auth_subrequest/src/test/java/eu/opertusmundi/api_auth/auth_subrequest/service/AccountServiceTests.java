package eu.opertusmundi.api_auth.auth_subrequest.service;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.opertusmundi.api_auth.model.AccountDto;

@QuarkusTest
public class AccountServiceTests
{
    @Inject
    @Named("defaultAccountService")
    AccountService accountService;
    
    @ParameterizedTest
    @ValueSource(strings = { "e296cd03-6a67-418e-af32-3a10e2957144" })
    public void findByKey(String keyAsString) 
    {
        Uni<AccountDto> accountUni =  accountService.findByKey(keyAsString);
        UniAssertSubscriber<AccountDto> accountUniAssert = accountUni.subscribe()
            .withSubscriber(UniAssertSubscriber.create());
        
        AccountDto account = accountUniAssert.awaitItem().getItem();
        assertEquals(keyAsString, account.getKey().toString());
    }
}
