package pl.piomin.services.common.model;

public class Order {

    private Long id;
    private Long sourceAccountId;
    private Long targetAccountId;
    private int amount;
    private String status;
    private Long groupId;

    public Order() {
    }

    public Order(Long id, Long sourceAccountId, Long targetAccountId, int amount, String status, Long groupId) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.status = status;
        this.groupId = groupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", sourceAccountId=" + sourceAccountId +
                ", targetAccountId=" + targetAccountId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
