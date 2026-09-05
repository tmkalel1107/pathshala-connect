package PathshalaConnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount = 0.0;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private String status = "COMPLETED";

    @Column(name = "payment_method")
    private String paymentMethod = "DEMO_UPI";

    @Column(name = "transaction_ref")
    private String transactionRef;

    @Column(name = "donation_date")
    private LocalDateTime donationDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id")
    private User donor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "need_id")
    private Need need;

    public Donation() {
    }

    public Donation(double amount, String message, User donor, School school, Need need) {
        this.amount = amount;
        this.message = message;
        this.donor = donor;
        this.school = school;
        this.need = need;
        this.status = "COMPLETED";
        this.paymentMethod = "DEMO_GATEWAY";
        this.donationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount != null ? amount : 0.0;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount != null ? amount : 0.0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public LocalDateTime getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDateTime donationDate) {
        this.donationDate = donationDate;
    }

    public User getDonor() {
        return donor;
    }

    public void setDonor(User donor) {
        this.donor = donor;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Need getNeed() {
        return need;
    }

    public void setNeed(Need need) {
        this.need = need;
    }
}