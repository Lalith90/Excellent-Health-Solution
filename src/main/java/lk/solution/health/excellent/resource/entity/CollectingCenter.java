package lk.solution.health.excellent.resource.entity;

import lk.solution.health.excellent.resource.entity.Enum.CollectingCenterStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "collecting_center")
@Getter
@Setter
public class CollectingCenter {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "name", length = 45)
    @Min(value = 1, message = "Should be need to include one character buddy !!")
    private String name;

    @Basic
    @Column(name = "owner",  length = 45)
    @Min(value = 1, message = "Should be need to include one character buddy !!")
    private String owner;

    @Basic
    @Column(name = "mobile",  length = 10)
    private String mobile;

    @Basic
    @Column(name = "land", length = 10)
    private String land;

    @Basic
    @Column(name = "email",  length = 45)
    @Email
    private String email;

    @Basic
    @Column(name = "address")
    @Min(value = 1, message = "Should be need to include one character buddy !!")
    private String address;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "established_date")
    private LocalDate establishedDate;

    @Enumerated(EnumType.STRING)
    private CollectingCenterStatus collectingCenterStatus;


/*    @OneToMany
    @JoinColumn(name = "collecting_center_id")
    private List<Invoice> invoices = new ArrayList<>();*/

    public CollectingCenter() {
    }

    public CollectingCenter(String name, String owner, String mobile, String land, String email, String address, LocalDate establishedDate, CollectingCenterStatus collectingCenterStatus) {
        this.name = name;
        this.owner = owner;
        this.mobile = mobile;
        this.land = land;
        this.email = email;
        this.address = address;
        this.establishedDate = establishedDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CollectingCenter)) return false;
        CollectingCenter collectingCenter = (CollectingCenter) obj;
        return Objects.equals(id, collectingCenter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
