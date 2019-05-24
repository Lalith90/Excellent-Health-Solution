package lk.solution.health.excellent.lab.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.lab.entity.Enum.Department;
import lk.solution.health.excellent.lab.entity.Enum.LabtestDoneHere;
import lk.solution.health.excellent.resource.entity.MedicalPackage;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "labtest")
@Getter
@Setter
@JsonFilter("LabTest")
public class LabTest {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "code", nullable = false, length = 6, unique = true)
    private String code;

    @Basic
    @Column(name = "name", nullable = false)
    private String name;

    @Basic
    @Column(name = "price", precision=10, scale=2)
    private BigDecimal price;

    @Basic
    @Column(name = "sample_collecting_tube", nullable = false, length = 20)
    private String sampleCollectingTube;

    @Column(name = "department", nullable = false)
    @Enumerated(EnumType.STRING)
    private Department department;


    @Enumerated(EnumType.STRING)
    private LabtestDoneHere labtestDoneHere;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "comment")
    private String comment;

    @OneToMany(mappedBy = "labTest")
    private List<InvoiceHasLabTest>  invoiceHasLabTests= new ArrayList<>();

    @ManyToMany(mappedBy = "labTests",fetch=FetchType.LAZY)
    private List<MedicalPackage> medicalPackages = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "labtest_has_parameter",
            joinColumns = @JoinColumn(name = "labtest_id"),
            inverseJoinColumns = @JoinColumn(name = "labtest_parameter_id"))
    private List<LabTestParameter> labTestParameters = new ArrayList<>();

    public LabTest() {
    }

    public LabTest(String code, String name, BigDecimal price, String sampleCollectingTube, Department department, LabtestDoneHere labtestDoneHere, String description, String comment) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.sampleCollectingTube = sampleCollectingTube;
        this.department = department;
        this.labtestDoneHere = labtestDoneHere;
        this.description = description;
        this.comment = comment;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LabTest)) return false;
        LabTest labTest = (LabTest) obj;
        return Objects.equals(id, labTest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
