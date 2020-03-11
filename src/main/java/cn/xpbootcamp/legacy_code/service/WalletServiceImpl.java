package cn.xpbootcamp.legacy_code.service;

import cn.xpbootcamp.legacy_code.entity.BusinessDeal;
import cn.xpbootcamp.legacy_code.entity.User;
import cn.xpbootcamp.legacy_code.enums.Status;
import cn.xpbootcamp.legacy_code.repository.UserRepository;

import java.util.UUID;

public class WalletServiceImpl implements WalletService {


    private final UserRepository userRepository;

    public WalletServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean handleTransaction(BusinessDeal deal) {
        if (deal.isExpired()) {
            return false;
        }
        return moveMoney(deal) != null;
    }

    private String moveMoney(BusinessDeal deal) {
        User buyer = userRepository.find(deal.getBuyerId());
        if (isBalanceNotEnough(deal, buyer)) {
            return null;
        }
        User seller = userRepository.find(deal.getSellerId());
        seller.increaseBalance(deal.getAmount());
        buyer.decreaseBalance(deal.getAmount());
        return UUID.randomUUID().toString() + deal.getId();
    }

    private boolean isBalanceNotEnough(BusinessDeal deal, User buyer) {
        return buyer.getBalance() < deal.getAmount();
    }
}
