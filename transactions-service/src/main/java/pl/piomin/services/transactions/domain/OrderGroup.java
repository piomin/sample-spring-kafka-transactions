package pl.piomin.services.transactions.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OrderGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;
    private int totalNoOfOrders;
    private int processedNoOfOrders;

    public OrderGroup() {
    }

    public OrderGroup(String status, int totalNoOfOrders, int processedNoOfOrders) {
        this.status = status;
        this.totalNoOfOrders = totalNoOfOrders;
        this.processedNoOfOrders = processedNoOfOrders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalNoOfOrders() {
        return totalNoOfOrders;
    }

    public void setTotalNoOfOrders(int totalNoOfOrders) {
        this.totalNoOfOrders = totalNoOfOrders;
    }

    public int getProcessedNoOfOrders() {
        return processedNoOfOrders;
    }

    public void setProcessedNoOfOrders(int processedNoOfOrders) {
        this.processedNoOfOrders = processedNoOfOrders;
    }

    @Override
    public String toString() {
        return "OrderGroup{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", totalNoOfOrders=" + totalNoOfOrders +
                ", processedNoOfOrders=" + processedNoOfOrders +
                '}';
    }
}
