package lk.solution.health.excellent.transaction.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "discount_ratio")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRatio {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Basic
    @Column(name = "amount", nullable = false, precision=10, scale=2)
    private BigDecimal amount;

/*    @OneToMany
    @JoinColumn(name = "discount_ratio_id")
    private List<Invoice> invoices = new ArrayList<>();*/


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DiscountRatio)) return false;
        DiscountRatio discountRatio = (DiscountRatio) obj;
        return Objects.equals(id, discountRatio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
