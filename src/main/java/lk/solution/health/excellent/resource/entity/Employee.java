package lk.solution.health.excellent.resource.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.general.entity.Enum.Gender;
import lk.solution.health.excellent.general.entity.Enum.Title;
import lk.solution.health.excellent.resource.entity.Enum.CivilStatus;
import lk.solution.health.excellent.resource.entity.Enum.Designation;
import lk.solution.health.excellent.resource.entity.Enum.EmployeeStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "employee")
@Getter
@Setter
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},allowGetters = true)
public class Employee {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "number", nullable = false)
    @NotNull(message = "Number is required")
    private String number;


    @Basic
    @Column(name = "title", nullable = false)
    @Enumerated(EnumType.STRING)
    private Title title;

    @Basic
    @Column(name = "name", nullable = false)
    @Size(min = 5, message = "Your name cannot be accept")
    @Pattern(regexp = "^([a-zA-Z\\\\s]{3,})$", message = " This name can not accept, Please check and try again")
    private String name;

    @Basic
    @Column(name = "calling_name", nullable = false)
    @Size(min = 5, message = "At least 5 characters should be include calling name")
    private String callingName;


    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Basic
    @Size(max = 12, min = 10, message = "NIC number is contained numbers between 9 and X/V or 12 ")
    @Pattern(regexp = "^([\\\\d]{9}[v|V|x|X])$|^([\\\\d]{12})$", message = "NIC number is contained numbers between 9 and X/V or 12 ")
    @Column(name = "nic", unique = true)
    private String nic;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "civil_status")
    @Enumerated(EnumType.STRING)
    private CivilStatus civilStatus;

    @Basic
    //@Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$",message = "Please provide valid email")
    @Email
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "mobile", nullable = false)
    @Size(min = 9, message = "Can not accept this mobile number")
    private String mobile;

    @Basic
    @Column(name = "land")
    private String land;

    @Basic
    @Column(name = "address", nullable = false)
    @Size(min = 5, message = "Should be need to provide valid address !!")
    private String address;


    @Column(name = "designation", nullable = false)
    @Enumerated(EnumType.STRING)
    private Designation designation;


    @Column(name = "employee_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeStatus employeeStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "doassignment",  nullable = false)
    private LocalDate doassignment;

    @Column(name = "created_at", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate updatedAt;


    public Employee() {
    }

    public Employee(String number, Title title, String name, String callingName, Gender gender, String nic, LocalDate dateOfBirth, CivilStatus civilStatus, String email, String mobile, String land, String address, Designation designation, EmployeeStatus employeeStatus, LocalDate doassignment, LocalDate createdAt, LocalDate updatedAt) {
        this.number = number;
        this.title = title;
        this.name = name;
        this.callingName = callingName;
        this.gender = gender;
        this.nic = nic;
        this.dateOfBirth = dateOfBirth;
        this.civilStatus = civilStatus;
        this.email = email;
        this.mobile = mobile;
        this.land = land;
        this.address = address;
        this.designation = designation;
        this.employeeStatus = employeeStatus;
        this.doassignment = doassignment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Employee)) return false;
        Employee employee = (Employee) obj;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
