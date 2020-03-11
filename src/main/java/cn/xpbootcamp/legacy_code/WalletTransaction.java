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
        validateDealParms(deal);
        if (isExecuted(deal)) return true;
        boolean isLocked = redisLock.lock(deal.getId());
        try {
            if (isExecuted(deal)) return true;
            if (!isLocked) {
                return false;
            }
            return walletService.handleTransaction(deal);
        } finally {
            if (isLocked) {
                redisLock.unlock(deal.getId());
            }
        }
    }

    private boolean isExecuted(BusinessDeal deal) {
        return deal.getStatus() == Status.EXECUTED;
    }

    private void validateDealParms(BusinessDeal deal) throws InvalidTransactionException {
        if (deal.getBuyerId() == null || (deal.getSellerId() == null || deal.getAmount() < 0.0)) {
            throw new InvalidTransactionException("This is an invalid transaction");
        }
    }

}