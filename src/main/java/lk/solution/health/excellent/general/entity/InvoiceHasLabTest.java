package lk.solution.health.excellent.general.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.entity.ResultTable;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"workSheetTakenDateTime", "resultEnteredDateTime", "sampleCollectedDateTime","reportAuthorizeDateTime","reportPrintedDateTime", "reportRePrintedDateTime", "sampleCollectingUser", "workSheetTakenUser", "resultEnteredUser", "reportAuthorizedUser", "reportPrintedUser", "reportRePrintedUser"},allowGetters = true)
public class InvoiceHasLabTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer number;

    private String comment;

    @OneToMany(mappedBy = "invoiceHasLabTest", fetch = FetchType.EAGER)
    private List<ResultTable> resultTables = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "labtest_id")
    private LabTest labTest;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LabTestStatus labTestStatus=LabTestStatus.NOSAMPLE;

    @Column(nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH})
    private User user, sampleCollectingUser, workSheetTakenUser, resultEnteredUser, reportAuthorizedUser, reportPrintedUser,reportRePrintedUser;

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


}
