package lk.solution.health.excellent.lab.entity;

import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "result_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ResultTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 7)
    private String result;

    @Column(length = 7)
    private String absoluteCount;

    @ManyToOne
    @JoinColumn(name = "invoice_has_lab_test_id")
    private InvoiceHasLabTest invoiceHasLabTest;

    @ManyToOne
    @JoinColumn(name = "labtest_id")
    private LabTest labTest;

    @ManyToOne
    @JoinColumn(name = "labtest_parameter_id")
    private LabTestParameter labTestParameter;


}
