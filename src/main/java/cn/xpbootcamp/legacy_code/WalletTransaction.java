package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.entity.BusinessDeal;
import cn.xpbootcamp.legacy_code.enums.Status;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;

import javax.transaction.InvalidTransactionException;

public class WalletTransaction {

    private final RedisDistributedLock redisLock;
    private final WalletService walletService;

    public WalletTransaction(RedisDistributedLock redisLock, WalletService walletService) {
        this.redisLock = redisLock;
        this.walletService = walletService;
    }


    public boolean execute(BusinessDeal deal) throws InvalidTransactionException {
        return deal.execute(redisLock, walletService);
    }

}