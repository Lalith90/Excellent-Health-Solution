package lk.solution.health.excellent.transaction.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.resource.entity.*;
import lk.solution.health.excellent.transaction.entity.Enum.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},allowGetters = true)
public class Invoice {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "payment_method", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Basic
    @Column(name = "totalprice",  precision=10, scale=2)
    private BigDecimal totalprice;

    @Basic
    @Column(name = "amount", nullable = false, precision=10, scale=2)
    private BigDecimal amount;

    @Basic
    @Column(name = "bank_name")
    private String bankName;

    @Basic
    @Column(name = "card_number")
    private Integer cardNumber;

    @Basic
    @Column(name = "remarks", length = 150)
    private String remarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH,
            CascadeType.DETACH})
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    private CollectingCenter collectingCenter;

    @ManyToOne
    private DiscountRatio discountRatio;

    @ManyToOne
    private User user;

    @ManyToOne
    private MedicalPackage medicalPackage;

    @ManyToOne
    private Doctor doctor;

    @OneToMany
    @JoinColumn(name = "invoice_id")
    private List<Refund> refunds = new ArrayList<>();


    @OneToMany
    @JoinColumn(name = "invoice_id")
    private List<InvoiceHasLabTest> invoiceHasLabTests = new ArrayList<>();

    public Invoice() {
    }


    public Invoice(PaymentMethod paymentMethod, BigDecimal totalprice, BigDecimal amount, String bankName, Integer cardNumber, String remarks, LocalDate createdAt, Patient patient, CollectingCenter collectingCenter, DiscountRatio discountRatio, User user, MedicalPackage medicalPackage, Doctor doctor) {
        this.paymentMethod = paymentMethod;
        this.totalprice = totalprice;
        this.amount = amount;
        this.bankName = bankName;
        this.cardNumber = cardNumber;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.patient = patient;
        this.collectingCenter = collectingCenter;
        this.discountRatio = discountRatio;
        this.user = user;
        this.medicalPackage = medicalPackage;
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Invoice)) return false;
        Invoice invoice = (Invoice) obj;
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



}
