package cn.xpbootcamp.legacy_code;

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
        WalletTransaction walletTransaction = new WalletTransaction("1", null, 1L, 1L, "1", null);
        assertThrows(InvalidTransactionException.class, walletTransaction::execute);
    }

    @Test
    void sellerId_is_null__throw_exception() {
        WalletTransaction walletTransaction = new WalletTransaction("1", 1L, null, 1L, "1", null);
        assertThrows(InvalidTransactionException.class, walletTransaction::execute);
    }

    @Test
    void amount_is_0__throw_exception() {
        WalletTransaction walletTransaction = new WalletTransaction("1", 1L, 1L, 1L, "1", null);
        walletTransaction.setAmount(-0.01);
        assertThrows(InvalidTransactionException.class, walletTransaction::execute);
    }

    @Test
    void locked_is_false__execute__return_false() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(false);

        WalletTransaction walletTransaction = new WalletTransaction("1", 1L, 1L, 1L, "1", redisDistributedLock);
        walletTransaction.setAmount(1.0);

        assertFalse(walletTransaction.execute());
    }

    @Test
    void status_is_exchanged__execute__return_true() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(true);
        WalletTransaction walletTransaction = new WalletTransaction("1", 1L, 1L, 1L, "1", redisDistributedLock);
        walletTransaction.setAmount(1.0);
        walletTransaction.setStatus(Status.EXECUTED);

        assertTrue(walletTransaction.execute());
    }

    @Test
    void status_is_unExchanged_and_expired_is_true__execute__return_false() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(true);
        WalletTransaction walletTransaction = new WalletTransaction("1", 1L, 1L, 1L, "1", redisDistributedLock);
        walletTransaction.setAmount(1.0);
        walletTransaction.setCurrentTimeMillis(1728001111L);
        walletTransaction.setCreatedTimestamp(0L);

        assertFalse(walletTransaction.execute());
        assertEquals(Status.EXPIRED, walletTransaction.getStatus());
    }


    @Test
    void balance_is_enough__execute__return_true() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(true);
        WalletTransaction walletTransaction = new WalletTransaction("1", 1L, 1L, 1L, "1", redisDistributedLock);
        walletTransaction.setAmount(1.0);
        walletTransaction.setCurrentTimeMillis(1728001111L);
        walletTransaction.setCreatedTimestamp(1728001111L);

        WalletService walletService = getWalletService(mockUserRepository(new User(1, 10.0)));

        walletTransaction.setWalletService(walletService);

        assertTrue(walletTransaction.execute());
    }

    @Test
    void balance_is_not_enough__execute__return_false() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(true);
        WalletTransaction walletTransaction = new WalletTransaction("1", 1L, 1L, 1L, "1", redisDistributedLock);
        walletTransaction.setAmount(30.0);
        walletTransaction.setCurrentTimeMillis(1728001111L);
        walletTransaction.setCreatedTimestamp(1728001111L);

        WalletService walletService = getWalletService(mockUserRepository(new User(1, 10.0)));

        walletTransaction.setWalletService(walletService);

        assertFalse(walletTransaction.execute());
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