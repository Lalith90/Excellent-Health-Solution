package lk.solution.health.excellent.transaction.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.resource.entity.User;
import lombok.*;
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
@AllArgsConstructor
@NoArgsConstructor
@ToString
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


    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;


    @Column(name = "reason", length = 45)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH,
                    CascadeType.DETACH})
    private User user;


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


}
