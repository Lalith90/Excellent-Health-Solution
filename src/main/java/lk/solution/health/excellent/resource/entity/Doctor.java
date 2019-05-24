package lk.solution.health.excellent.resource.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.general.entity.Consultation;
import lk.solution.health.excellent.general.entity.Enum.Gender;
import lk.solution.health.excellent.general.entity.Enum.Title;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "doctor")
@Getter
@Setter
@JsonIgnoreProperties(value = ("updateDate"),allowGetters = true)
public class Doctor {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    @Enumerated(EnumType.STRING)
    private Title title;

    @Basic
    @Column(name = "name", length = 45)
    private String name;

    @Basic
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(fetch = FetchType.EAGER)
    private Consultation consultation;

    @Basic
    @Column(name = "slmc_number", unique = true)
    private Integer slmcNumber;

    @Basic
    @Column(name = "mobile")
    private String mobile = "No mobile number";

    @Basic
    @Column(name = "land", length = 10)
    private String land;

    @Basic
    @Email(message = "Please provide valid email")
    @Column(name = "email", length = 45)
    private String email;

    @Basic
    @Column(name = "description", length = 10)
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "create_date", nullable = false)
    private LocalDate createAt;


/*    @OneToMany(mappedBy = "doctor")
    private List<Invoice> invoices = new ArrayList<>();*/

    public Doctor() {
    }

    public Doctor(Title title, String name, Gender gender, Consultation consultation, Integer slmcNumber, String mobile, String land, String email, LocalDate createAt) {
        this.title = title;
        this.name = name;
        this.gender = gender;
        this.consultation = consultation;
        this.slmcNumber = slmcNumber;
        this.mobile = mobile;
        this.land = land;
        this.email = email;
        this.createAt = createAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Doctor)) return false;
        Doctor doctor = (Doctor) obj;
        return Objects.equals(id, doctor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
