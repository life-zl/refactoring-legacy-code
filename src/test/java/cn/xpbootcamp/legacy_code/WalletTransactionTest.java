package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.transaction.InvalidTransactionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void locked_is_true__execute__return_false() throws InvalidTransactionException {

        when(redisDistributedLock.lock(anyString())).thenReturn(true);

        WalletTransaction walletTransaction = new WalletTransaction("1", 1L, 1L, 1L, "1", redisDistributedLock);
        walletTransaction.setAmount(1.0);

        assertFalse(walletTransaction.execute());
    }



}