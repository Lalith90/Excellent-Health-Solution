package lk.solution.health.excellent.lab.entity;

import lk.solution.health.excellent.lab.entity.Enum.ParameterHeader;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "labtest_parameter")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class LabTestParameter {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", length = 45, unique = true)
    private String code;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "unit", length = 45)
    private String unit;

    @Column(name = "max", length = 6)
    private String max;

    @Column(name = "min", length = 7)
    private String min;

    @Enumerated(EnumType.STRING)
    private ParameterHeader parameterHeader;

    @ManyToMany(mappedBy = "labTestParameters",fetch=FetchType.LAZY)
    private List<LabTest> labTests = new ArrayList<>();


}
