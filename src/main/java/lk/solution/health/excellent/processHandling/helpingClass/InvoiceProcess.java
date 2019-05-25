package lk.solution.health.excellent.processHandling.helpingClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.resource.entity.CollectingCenter;
import lk.solution.health.excellent.resource.entity.Doctor;
import lk.solution.health.excellent.resource.entity.MedicalPackage;
import lk.solution.health.excellent.resource.entity.Patient;
import lk.solution.health.excellent.transaction.entity.DiscountRatio;
import lk.solution.health.excellent.transaction.entity.Enum.InvoicePrintOrNot;
import lk.solution.health.excellent.transaction.entity.Enum.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(value = {"medicalPackage", "labTests"})
public class InvoiceProcess {

    private List<LabTest> labTests;

    private MedicalPackage medicalPackage;

    @NotNull(message = " Patient is need to invoice")
    private Patient patient;

    private Doctor doctor;
    @NotNull(message = "collecting center is need ")
    private CollectingCenter collectingCenter;
    private DiscountRatio discountRatio;

    private String remarks, bankName;
    private Integer cardNumber;

    @Enumerated(EnumType.STRING)
    private InvoicePrintOrNot invoicePrintOrNot;

    @NotNull(message = "Please double check price and amount")
    private BigDecimal totalprice, amount, amountTendered, balance;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    public InvoiceProcess() {
    }


}
