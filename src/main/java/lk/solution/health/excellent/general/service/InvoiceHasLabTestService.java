package lk.solution.health.excellent.general.service;

import lk.solution.health.excellent.general.dao.InvoiceHasLabTestDao;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lk.solution.health.excellent.util.interfaces.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceHasLabTestService implements AbstractService<InvoiceHasLabTest, Integer> {

    private final InvoiceHasLabTestDao invoiceHasLabTestDao;


    @Autowired
    public InvoiceHasLabTestService(InvoiceHasLabTestDao invoiceHasLabTestDao) {
        this.invoiceHasLabTestDao = invoiceHasLabTestDao;
    }

    public List<InvoiceHasLabTest> findAll() {
        return invoiceHasLabTestDao.findAll();
    }

    public void persistBulk(List<InvoiceHasLabTest> invoiceHasLabTests) {
        invoiceHasLabTestDao.saveAll(invoiceHasLabTests);
    }

    public InvoiceHasLabTest findById(Integer id) {
        return invoiceHasLabTestDao.getOne(id);
    }

    public InvoiceHasLabTest persist(InvoiceHasLabTest invoiceHasLabTest) {
        return invoiceHasLabTestDao.save(invoiceHasLabTest);
    }

    public boolean delete(Integer id) {
        invoiceHasLabTestDao.deleteById(id);
        return true;
    }

    public List<InvoiceHasLabTest> search(InvoiceHasLabTest invoiceHasLabTest) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<InvoiceHasLabTest> invoiceHasLabTestExample = Example.of(invoiceHasLabTest, matcher);
        return invoiceHasLabTestDao.findAll(invoiceHasLabTestExample);
    }


    public InvoiceHasLabTest findLastInvoiceHasLabTest() {
        return invoiceHasLabTestDao.findFirstByOrderByIdDesc();
    }


    public List<InvoiceHasLabTest> findByLabTestState(LabTestStatus labTestStatus) {
        return invoiceHasLabTestDao.findByLabTestStatus(labTestStatus);
    }


    public List<InvoiceHasLabTest> findByInvoiceAndLabTestStatus(Invoice invoice, LabTestStatus labTestStatus) {
        return invoiceHasLabTestDao.findByInvoiceAndLabTestStatus(invoice, labTestStatus);
    }


    public InvoiceHasLabTest findByInvoiceAndLabTest(Invoice invoice, LabTest labTest) {
        return invoiceHasLabTestDao.findByInvoiceAndLabTest(invoice, labTest);
    }


    public List<InvoiceHasLabTest> findByInvoice(Invoice invoice) {
        return invoiceHasLabTestDao.findByInvoice(invoice);
    }

    public List<LabTest> findLabTestByInvoice(Invoice invoice) {
        List<LabTest> labTests = new ArrayList<>();
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTestDao.findByInvoice(invoice))
            labTests.add(invoiceHasLabTest.getLabTest());
        return labTests;
    }

    public List<InvoiceHasLabTest> findByDate(LocalDate createdAt) {
        return invoiceHasLabTestDao.findByCreatedAt(createdAt);
    }


    public List<InvoiceHasLabTest> findByCreatedAtIsBetween(LocalDate from, LocalDate to) {
        return invoiceHasLabTestDao.findByCreatedAtIsBetween(from, to);
    }

    public InvoiceHasLabTest findByNumber(int number) {
        return invoiceHasLabTestDao.findByNumber(number);
    }

    public List<InvoiceHasLabTest> findByDateAndUser(LocalDate date, User user) {
        return invoiceHasLabTestDao.findByCreatedAtAndUser(date, user);
    }

    public List<InvoiceHasLabTest> findByInvoiceAndCreatedAtIsBetween(LocalDate from, LocalDate to,Invoice invoice){
        return invoiceHasLabTestDao.findByCreatedAtIsBetweenAndInvoice(from,to,invoice);
    }
}


