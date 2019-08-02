package lk.solution.health.excellent.lab.dao;

import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.lab.entity.ResultTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultTableDao extends JpaRepository<ResultTable, Integer> {
    ResultTable findFirstByOrderByIdDesc();

    List<ResultTable> findByInvoiceHasLabTest(InvoiceHasLabTest invoiceHasLabTest);

}
