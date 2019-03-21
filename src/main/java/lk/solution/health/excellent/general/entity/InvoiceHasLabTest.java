package lk.solution.health.excellent.general.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_has_Labtest")
@Getter
@Setter
@JsonIgnoreProperties(value = {"workSheetTakenDateTime", "resultEnteredDateTime", "sampleCollectedDateTime","reportAuthorizeDateTime","reportPrintedDateTime","reportRePrintedDateTime",
        "sampleCollectingUser", "workSheetTakenUser", "resultEnteredUser", "reportAuthorizedUser", "reportPrintedUser", "reportRePrintedUser"},allowGetters = true)
public class InvoiceHasLabTest {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "labtest_id")
    private LabTest labTest;

    @Column(name = "lab_test_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LabTestStatus labTestStatus=LabTestStatus.NOSAMPLE;

    @Column(name = "created_at", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH})
    private User user,sampleCollectingUser, workSheetTakenUser, resultEnteredUser, reportAuthorizedUser, reportPrintedUser,reportRePrintedUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sampleCollectedDateTime;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime workSheetTakenDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resultEnteredDateTime;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportAuthorizeDateTime;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportPrintedDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportRePrintedDateTime;

    public InvoiceHasLabTest() {
    }

    public InvoiceHasLabTest(Invoice invoice, LabTest labTest, LabTestStatus labTestStatus, LocalDate createdAt) {
        this.invoice = invoice;
        this.labTest = labTest;
        this.labTestStatus = labTestStatus;
        this.createdAt = createdAt;
    }
}
