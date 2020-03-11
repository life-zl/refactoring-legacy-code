package cn.xpbootcamp.legacy_code.service;

import cn.xpbootcamp.legacy_code.entity.BusinessDeal;
import cn.xpbootcamp.legacy_code.entity.User;
import cn.xpbootcamp.legacy_code.enums.Status;
import cn.xpbootcamp.legacy_code.repository.UserRepository;

import java.util.UUID;

public class WalletServiceImpl implements WalletService {

    public static final int EXPIRED_TIME = 20 * 24 * 60 * 60;

    private final UserRepository userRepository;

    public WalletServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean handleTransaction(BusinessDeal deal) {
        if (deal.getCurrentTimeMillis() - deal.getCreatedTimestamp() > EXPIRED_TIME) {
            return false;
        }
        return moveMoney(deal) != null;
    }


    public String moveMoney(BusinessDeal deal) {
        User buyer = userRepository.find(deal.getBuyerId());
        if (!(buyer.getBalance() >= deal.getAmount())) {
            return null;
        }
        User seller = userRepository.find(deal.getSellerId());
        seller.setBalance(seller.getBalance() + deal.getAmount());
        buyer.setBalance(buyer.getBalance() - deal.getAmount());
        return UUID.randomUUID().toString() + deal.getId();
    }
}
