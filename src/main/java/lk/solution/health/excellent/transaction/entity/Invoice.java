package lk.solution.health.excellent.transaction.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.resource.entity.*;
import lk.solution.health.excellent.transaction.entity.Enum.InvoicePrintOrNot;
import lk.solution.health.excellent.transaction.entity.Enum.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public class Invoice {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "number", nullable = false, unique = true)
    private Integer number;

    @Column(name = "payment_method", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;


    @Column(name = "totalprice", precision = 10, scale = 2)
    private BigDecimal totalprice;


    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;


    @Column(name = "bank_name")
    private String bankName;


    @Column(name = "card_number")
    private Integer cardNumber;


    @Column(name = "remarks", length = 150)
    private String remarks;

    @Enumerated(EnumType.STRING)
    private InvoicePrintOrNot invoicePrintOrNot;

    @Column(name = "created_at", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @ManyToOne
    private Patient patient;

    @ManyToOne
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
    private List<InvoiceHasLabTest> invoiceHasLabTests = new ArrayList<>();

    public Invoice() {
    }

    public Invoice(Integer number, PaymentMethod paymentMethod, BigDecimal totalprice, BigDecimal amount, String bankName, Integer cardNumber, String remarks, InvoicePrintOrNot invoicePrintOrNot, LocalDateTime createdAt, Patient patient, CollectingCenter collectingCenter, DiscountRatio discountRatio, User user, MedicalPackage medicalPackage, Doctor doctor) {
        this.number = number;
        this.paymentMethod = paymentMethod;
        this.totalprice = totalprice;
        this.amount = amount;
        this.bankName = bankName;
        this.cardNumber = cardNumber;
        this.remarks = remarks;
        this.invoicePrintOrNot = invoicePrintOrNot;
        this.createdAt = createdAt;
        this.patient = patient;
        this.collectingCenter = collectingCenter;
        this.discountRatio = discountRatio;
        this.user = user;
        this.medicalPackage = medicalPackage;
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", number=" + number +
                ", paymentMethod=" + paymentMethod +
                ", totalprice=" + totalprice +
                ", amount=" + amount +
                ", bankName='" + bankName + '\'' +
                ", cardNumber=" + cardNumber +
                ", remarks='" + remarks + '\'' +
                ", invoicePrintOrNot=" + invoicePrintOrNot +
                ", createdAt=" + createdAt +
                ", patient=" + patient +
                ", collectingCenter=" + collectingCenter +
                ", discountRatio=" + discountRatio +
                ", user=" + user +
                ", medicalPackage=" + medicalPackage +
                ", doctor=" + doctor +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(BigDecimal totalprice) {
        this.totalprice = totalprice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Integer getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Integer cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public InvoicePrintOrNot getInvoicePrintOrNot() {
        return invoicePrintOrNot;
    }

    public void setInvoicePrintOrNot(InvoicePrintOrNot invoicePrintOrNot) {
        this.invoicePrintOrNot = invoicePrintOrNot;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public CollectingCenter getCollectingCenter() {
        return collectingCenter;
    }

    public void setCollectingCenter(CollectingCenter collectingCenter) {
        this.collectingCenter = collectingCenter;
    }

    public DiscountRatio getDiscountRatio() {
        return discountRatio;
    }

    public void setDiscountRatio(DiscountRatio discountRatio) {
        this.discountRatio = discountRatio;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MedicalPackage getMedicalPackage() {
        return medicalPackage;
    }

    public void setMedicalPackage(MedicalPackage medicalPackage) {
        this.medicalPackage = medicalPackage;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
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
