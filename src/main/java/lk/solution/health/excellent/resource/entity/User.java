package lk.solution.health.excellent.resource.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@Setter
@JsonIgnoreProperties(value ="createdDate", allowGetters = true)
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Basic
    @Column(name = "username", nullable = false, length = 45)
    @Size(min = 5, message = "user name should include at least five characters")
    private String username;

    @Basic
    @Column(name = "password", nullable = false, length = 45)
    @Size(min = 4, message = "password should include four characters or symbols")
    private String password;

    @Basic
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "created_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH})
    private Role role;

    public User() {
    }

    public User(Employee employee, String username, String password, boolean enabled, LocalDate createdDate, Role role) {
        this.employee = employee;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.createdDate = createdDate;
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
