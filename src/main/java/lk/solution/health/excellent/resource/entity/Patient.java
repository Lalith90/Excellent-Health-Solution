package lk.solution.health.excellent.resource.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.general.entity.Enum.Gender;
import lk.solution.health.excellent.general.entity.Enum.Title;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patient")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(value = {"createdAt", "updatedAt", "createdUser", "upDateUser"}, allowGetters = true)
@JsonFilter("Patient")
public class Patient {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "number", nullable = false, unique = true)
    @NotNull(message = "This code is already add or enter incorrectly")
    private String number;


    @Column(name = "title")
    @Enumerated(EnumType.STRING)
    private Title title;

    @Column(name = "name", nullable = false, length = 45)
    @Pattern(regexp = "^([a-zA-Z\\s]{4,})$", message = " This name can not accept, Please check and try again, Name should be included more than four ccharacter")
    private String name;


    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "nic", length = 12, unique = true)
    @Pattern(regexp = "^([\\d]{9}[v|V|x|X])$|^([\\d]{12})$", message = "NIC number is contained numbers between 9 and X/V or 12 ")
    private String nic;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth")
    @NotNull(message = "Birthday should be included")
    private LocalDate dateOfBirth;

    @Column(name = "email", length = 45)
    @Email(message = "Please provide a valid Email")
    private String email;

    @Column(name = "mobile", length = 10)
    @Min(value = 9, message = "Should be needed to enter valid mobile number")
    private String mobile;

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

    @ManyToOne
    private User createdUser, upDateUser;

}
