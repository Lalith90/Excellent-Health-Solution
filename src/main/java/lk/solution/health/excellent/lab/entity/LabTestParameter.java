package lk.solution.health.excellent.lab.entity;

import lk.solution.health.excellent.lab.entity.Enum.ParameterHeader;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "labtest_parameter")
@Getter
@Setter
public class LabTestParameter {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "code", length = 45, unique = true)
    private String code;

    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Basic
    @Column(name = "unit", length = 45)
    private String unit;

    @Basic
    @Column(name = "max", length = 6)
    private String max;

    @Basic
    @Column(name = "min", length = 7)
    private String min;

    @Enumerated(EnumType.STRING)
    private ParameterHeader parameterHeader;


    @ManyToMany(mappedBy = "labTestParameters",fetch=FetchType.LAZY)
    private List<LabTest> labTests = new ArrayList<>();

    public LabTestParameter() {
    }

    public LabTestParameter(String code, String name, String unit, String max, String min, ParameterHeader parameterHeader) {
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.max = max;
        this.min = min;
        this.parameterHeader = parameterHeader;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LabTestParameter)) return false;
        LabTestParameter labtestParameter = (LabTestParameter) obj;
        return Objects.equals(id, labtestParameter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
