package cn.xpbootcamp.legacy_code.entity;

import cn.xpbootcamp.legacy_code.enums.Status;
import cn.xpbootcamp.legacy_code.utils.IdGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessDeal {
    private String id;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private String orderId;
    private Long createdTimestamp;
    private Double amount;
    private Status status = Status.EXPIRED;
    private String walletTransactionId;
    private long currentTimeMillis;

    public static final int EXPIRED_TIME = 20 * 24 * 60 * 60;


    public BusinessDeal(String id, Long buyerId, Long sellerId, Long productId, String orderId, Double amount) {
        this.id = IdGenerator.generateTransactionId();
        if (!this.id.startsWith("t_")) {
            this.id = "t_" + id;
        }
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public boolean isExpired() {
        return currentTimeMillis - createdTimestamp > EXPIRED_TIME;
    }
}
