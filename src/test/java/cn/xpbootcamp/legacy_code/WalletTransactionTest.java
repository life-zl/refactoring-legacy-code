package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.entity.BusinessDeal;
import cn.xpbootcamp.legacy_code.entity.User;
import cn.xpbootcamp.legacy_code.enums.Status;
import cn.xpbootcamp.legacy_code.repository.UserRepository;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;
import org.junit.jupiter.api.Test;

import javax.transaction.InvalidTransactionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WalletTransactionTest {

    RedisDistributedLock redisDistributedLock = mock(RedisDistributedLock.class);

    @Test
    void buyerId_is_null__throw_exception() {
        WalletTransaction walletTransaction = new WalletTransaction(null, null);
        BusinessDeal businessDeal = new BusinessDeal("1", null, 1L, 1L, null, 1.0);
        assertThrows(InvalidTransactionException.class, () -> walletTransaction.execute(businessDeal));
    }

    @Test
    void sellerId_is_null__throw_exception() {
        WalletTransaction walletTransaction = new WalletTransaction(null, null);
        BusinessDeal businessDeal = new BusinessDeal("1", 1L, null, 1L, null, 1.0);
        assertThrows(InvalidTransactionException.class, () -> walletTransaction.execute(businessDeal));
    }

    @Test
    void amount_is_0__throw_exception() {
        WalletTransaction walletTransaction = new WalletTransaction(null, null);
        BusinessDeal businessDeal = new BusinessDeal("1", 1L, 1L, 1L, null, -1.0);
        assertThrows(InvalidTransactionException.class, () -> walletTransaction.execute(businessDeal));
    }

    @Test
    void locked_is_false__execute__return_false() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(false);

        WalletTransaction walletTransaction = new WalletTransaction(redisDistributedLock, null);
        BusinessDeal businessDeal = new BusinessDeal("1", 1L, 1L, 1L, "1", 1.0);

        assertFalse(walletTransaction.execute(businessDeal));
    }

    @Test
    void status_is_exchanged__execute__return_true() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(true);

        WalletTransaction walletTransaction = new WalletTransaction(redisDistributedLock, null);
        BusinessDeal businessDeal = new BusinessDeal("1", 1L, 1L, 1L, "1", 1.0);
        businessDeal.setStatus(Status.EXECUTED);

        assertTrue(walletTransaction.execute(businessDeal));
    }

    @Test
    void status_is_unExchanged_and_expired_is_true__execute__return_false() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(true);


        WalletTransaction walletTransaction = new WalletTransaction(redisDistributedLock, null);
        BusinessDeal businessDeal = new BusinessDeal("1", 1L, 1L, 1L, "1", 1.0);
        businessDeal.setAmount(1.0);
        businessDeal.setCurrentTimeMillis(1728001111L);
        businessDeal.setCreatedTimestamp(0L);

        assertFalse(walletTransaction.execute(businessDeal));
        assertEquals(Status.EXPIRED, businessDeal.getStatus());
    }


    @Test
    void balance_is_enough__execute__return_true() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(true);

        WalletTransaction walletTransaction = new WalletTransaction(redisDistributedLock,
                getWalletService(mockUserRepository(new User(1, 10.0))));


        BusinessDeal businessDeal = new BusinessDeal("1", 1L, 1L, 1L, "1", 1.0);
        businessDeal.setAmount(1.0);
        businessDeal.setCurrentTimeMillis(1728001111L);
        businessDeal.setCreatedTimestamp(1728001111L);

        assertTrue(walletTransaction.execute(businessDeal));
    }

    @Test
    void balance_is_not_enough__execute__return_false() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(true);

        WalletTransaction walletTransaction = new WalletTransaction(redisDistributedLock,
                getWalletService(mockUserRepository(new User(1, 10.0))));


        BusinessDeal businessDeal = new BusinessDeal("1", 1L, 1L, 1L, "1", 1.0);
        businessDeal.setAmount(30.0);
        businessDeal.setCurrentTimeMillis(1728001111L);
        businessDeal.setCreatedTimestamp(1728001111L);



        assertFalse(walletTransaction.execute(businessDeal));
    }

    private WalletServiceImpl getWalletService(UserRepository userRepository) {
        return new WalletServiceImpl(userRepository);
    }

    private UserRepository mockUserRepository(User value) {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1)).thenReturn(value);
        return userRepository;
    }


}