package lk.solution.health.excellent.processHandling.helpingClass;

import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.entity.LabTestParameter;
import lk.solution.health.excellent.resource.entity.MedicalPackage;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SearchProcess {
    Integer id;
    String name, number, code, comment, nic;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate, endDate;
    List<LabTest> labTests;
    List<InvoiceHasLabTest> invoiceHasLabTests;
    List<LabTestParameter> labTestParameters;
    List<MedicalPackage> medicalPackages;
    List<String>result;
    List<String>absoluteCount;
    List<CustomTestFind> customTestFinds;


}
