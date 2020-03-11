package cn.xpbootcamp.legacy_code.service;

import cn.xpbootcamp.legacy_code.entity.BusinessDeal;

public interface WalletService {

    boolean handleTransaction(BusinessDeal deal);
}
