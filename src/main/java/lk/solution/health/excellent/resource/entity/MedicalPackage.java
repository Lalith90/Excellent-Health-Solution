package lk.solution.health.excellent.resource.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.resource.entity.Enum.MedicalPackageStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "medical_package")
@Getter
@Setter
@ToString
@JsonIgnoreProperties(value = "createdDate",allowGetters = true)
@JsonFilter("MedicalPackage")
public class MedicalPackage {
    @Id
    @Column(name = "id",updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Basic
    @Column(name = "price",  precision=10, scale=2, nullable = false)
    private BigDecimal price;

    @Column(name = "created_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;


    @Column(name = "medical_package_status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private MedicalPackageStatus medicalPackageStatus;

    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST,
            CascadeType.REFRESH,CascadeType.DETACH})
    @JoinTable(name = "medical_package_has_labtest",
            joinColumns = @JoinColumn(name = "medical_package_id"),
            inverseJoinColumns = @JoinColumn(name = "labtest_id"))
    private List<LabTest> labTests = new ArrayList<>();

    public MedicalPackage() {
    }

    public MedicalPackage(String name, BigDecimal price, LocalDate createdDate, MedicalPackageStatus medicalPackageStatus) {
        this.name = name;
        this.price = price;
        this.createdDate = createdDate;
        this.medicalPackageStatus = medicalPackageStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MedicalPackage)) return false;
        MedicalPackage medicalPackage = (MedicalPackage) obj;
        return Objects.equals(id, medicalPackage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
