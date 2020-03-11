package cn.xpbootcamp.legacy_code.entity;

public class User {
    private long id;
    private double balance;

    public User(long id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void increaseBalance(double amount) {
        balance += amount;
    }

    public void decreaseBalance(double amount) {
        balance += amount;
    }
}
