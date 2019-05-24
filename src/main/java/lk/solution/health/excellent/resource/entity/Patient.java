package lk.solution.health.excellent.resource.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.general.entity.Enum.Gender;
import lk.solution.health.excellent.general.entity.Enum.Title;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "patient")
@Getter
@Setter
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},allowGetters = true)
@JsonFilter("Patient")
public class Patient {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "number", nullable = false, unique = true)
    @NotNull(message = "This code is already add or enter incorrectly")
    private String number;


    @Column(name = "title")
    @Enumerated(EnumType.STRING)
    private Title title;

    @Basic
    @Column(name = "name", nullable = false, length = 45)
    @Pattern(regexp = "^([a-zA-Z\\\\s]{3,})$", message = " This name can not accept, Please check and try again")
    private String name;


    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Basic
    @Column(name = "nic", length = 12, unique = true)
    @Pattern(regexp = "^([\\\\d]{9}[v|V|x|X])$|^([\\\\d]{12})$", message = "NIC number is contained numbers between 9 and X/V or 12 ")
    private String nic;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth")
    @NotNull(message = "Birthday should be included")
    private LocalDate dateOfBirth;

    @Basic
    @Column(name = "email", length = 45)
    @Email(message = "Please provide a valid Email")
    private String email;

    @Basic
    @Column(name = "mobile", length = 10)
    @Min(value = 9, message = "Should be needed to enter valid mobile number")
    private String mobile;

    @Basic
    @Column(name = "land", length = 10)
    private String land;

    @Column(name = "created_at", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate updatedAt;

    @OneToMany
    @JoinColumn(name = "patient_id")
    private List<Invoice> invoices = new ArrayList<>();

    public Patient() {
     }

    public Patient(String number, Title title, String name, Gender gender, String nic, LocalDate dateOfBirth, String email, String mobile, String land, LocalDate createdAt, LocalDate updatedAt) {
        this.number = number;
        this.title = title;
        this.name = name;
        this.gender = gender;
        this.nic = nic;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.mobile = mobile;
        this.land = land;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", title=" + title +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", nic='" + nic + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", land='" + land + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Patient)) return false;
        Patient patient = (Patient) obj;
        return Objects.equals(id, patient.id);
}

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
