package pl.piomin.services.transactions.domain;

import pl.piomin.services.common.model.Order;

public class OrderDTO {

    private Long id;
    private Account sourceAccount;
    private Account targetAccount;
    private int amount;
    private String status;

    public OrderDTO(Order order, Account sourceAccount, Account targetAccount) {
        this.id = order.getId();
        this.amount = order.getAmount();
        this.status = order.getStatus();
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(Account targetAccount) {
        this.targetAccount = targetAccount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", sourceAccount=" + sourceAccount +
                ", targetAccount=" + targetAccount +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
