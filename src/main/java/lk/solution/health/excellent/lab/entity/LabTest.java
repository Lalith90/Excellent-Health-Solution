package lk.solution.health.excellent.lab.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.lab.entity.Enum.Department;
import lk.solution.health.excellent.lab.entity.Enum.LabtestDoneHere;
import lk.solution.health.excellent.resource.entity.MedicalPackage;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "labtest")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonFilter("LabTest")
public class LabTest {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, length = 6, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", precision=10, scale=2)
    private BigDecimal price;

    @Column(name = "sample_collecting_tube", nullable = false, length = 20)
    private String sampleCollectingTube;

    @Column(name = "department", nullable = false)
    @Enumerated(EnumType.STRING)
    private Department department;


    @Enumerated(EnumType.STRING)
    private LabtestDoneHere labtestDoneHere;

    @Column(name = "description")
    private String description;

    @Column(name = "comment")
    private String comment;

    @OneToMany(mappedBy = "labTest", fetch = FetchType.EAGER)
    private List<InvoiceHasLabTest>  invoiceHasLabTests= new ArrayList<>();

    @ManyToMany(mappedBy = "labTests",fetch=FetchType.LAZY)
    private List<MedicalPackage> medicalPackages = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "labtest_has_parameter",
            joinColumns = @JoinColumn(name = "labtest_id"),
            inverseJoinColumns = @JoinColumn(name = "labtest_parameter_id"))
    private List<LabTestParameter> labTestParameters = new ArrayList<>();

}
