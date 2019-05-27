package lk.solution.health.excellent.transaction.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.resource.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "refund")
@Getter
@Setter
//@JsonIgnoreProperties annotation is a Jackson annotation. Spring Boot uses Jackson for Serializing and Deserialize Java objects to and from JSON.
@JsonIgnoreProperties(value = "createdAt", allowGetters = true)
//implements Serializable
public class Refund {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH,
                    CascadeType.DETACH})
    private Invoice invoice;

    @Basic
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Basic
    @Column(name = "reason", length = 45)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH,
                    CascadeType.DETACH})
    private User user;


    public Refund() {
    }

    public Refund(Invoice invoice, String reason, LocalDateTime createdAt) {
        this.invoice = invoice;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Refund)) return false;
        Refund refund = (Refund) obj;
        return Objects.equals(id, refund.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Refund{" +
                "id=" + id +
                ", invoice=" + invoice +
                ", amount=" + amount +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                ", user=" + user +
                '}';
    }
}
