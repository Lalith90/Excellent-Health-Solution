package lk.solution.health.excellent.general.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "consultation")
@Getter
@Setter
public class Consultation {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;


    public Consultation() {
    }

    public Consultation(String name) {
        this.name = name;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Consultation)) return false;
        Consultation consultation = (Consultation) obj;
        return Objects.equals(id, consultation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}