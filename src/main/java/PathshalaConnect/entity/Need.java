package PathshalaConnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "needs")
public class Need {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(length = 2500, nullable = false)
    private String description;

    @Column(name = "target_amount", nullable = false)
    private Double targetAmount = 0.0;

    @Column(name = "received_amount")
    private Double receivedAmount = 0.0;

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN, FUNDED, CLOSED

    private String priority = "NORMAL"; // NORMAL, HIGH, URGENT

    private String deadline;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "need", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Donation> donations = new ArrayList<>();

    public Need() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTargetAmount() {
        return targetAmount != null ? targetAmount : 0.0;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setTargetAmount(Double targetAmount) {
        this.targetAmount = targetAmount != null ? targetAmount : 0.0;
    }

    public double getReceivedAmount() {
        return receivedAmount != null ? receivedAmount : 0.0;
    }

    public void setReceivedAmount(double receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public void setReceivedAmount(Double receivedAmount) {
        this.receivedAmount = receivedAmount != null ? receivedAmount : 0.0;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    public int getPercentRaised() {
        double target = getTargetAmount();
        if (target <= 0) return 0;
        int pct = (int) Math.round((getReceivedAmount() / target) * 100.0);
        return Math.min(100, Math.max(0, pct));
    }

    public double getRemainingAmount() {
        return Math.max(0.0, getTargetAmount() - getReceivedAmount());
    }
}