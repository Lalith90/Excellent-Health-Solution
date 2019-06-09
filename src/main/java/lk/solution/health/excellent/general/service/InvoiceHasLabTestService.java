package lk.solution.health.excellent.general.service;

import lk.solution.health.excellent.common.interfaces.AbstractService;
import lk.solution.health.excellent.general.dao.InvoiceHasLabTestDao;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.lab.dao.LabTestDao;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.lab.entity.Enum.LabtestDoneHere;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.transaction.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceHasLabTestService implements AbstractService<InvoiceHasLabTest, Integer> {

    private final InvoiceHasLabTestDao invoiceHasLabTestDao;
    private final LabTestDao labTestDao;


    @Autowired
    public InvoiceHasLabTestService(InvoiceHasLabTestDao invoiceHasLabTestDao, LabTestDao labTestDao) {
        this.invoiceHasLabTestDao = invoiceHasLabTestDao;
        this.labTestDao = labTestDao;
    }

    @Cacheable(value = "invoiceHasLabTest")
    @Transactional
    public List<InvoiceHasLabTest> findAll() {
        System.out.println(" Invoice has Lab Test cache ok");
        return invoiceHasLabTestDao.findAll();
    }

    @CachePut(value = "invoiceHasLabTest")
    @Transactional
    public InvoiceHasLabTest findById(Integer id) {
        return invoiceHasLabTestDao.getOne(id);
    }

    @CachePut(value = "invoiceHasLabTest")
    @Transactional
    public InvoiceHasLabTest persist(InvoiceHasLabTest invoiceHasLabTest) {
        return invoiceHasLabTestDao.save(invoiceHasLabTest);
    }

    @CacheEvict(value = "invoiceHasLabTest")
    public boolean delete(Integer id) {
        invoiceHasLabTestDao.deleteById(id);
        return true;
    }

    @CachePut(value = "invoiceHasLabTest")
    public List<InvoiceHasLabTest> search(InvoiceHasLabTest invoiceHasLabTest) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<InvoiceHasLabTest> invoiceHasLabTestExample = Example.of(invoiceHasLabTest, matcher);
        return invoiceHasLabTestDao.findAll(invoiceHasLabTestExample);
    }

    @CachePut(value = "invoiceHasLabTest")
    public InvoiceHasLabTest findLastInvoiceHasLabTest() {
        return invoiceHasLabTestDao.findFirstByOrderByIdDesc();
    }

    @CachePut(value = "invoiceHasLabTest")
    public List<InvoiceHasLabTest> findByLabTestState(LabTestStatus nosample) {
        return invoiceHasLabTestDao.findByLabTestStatus(nosample);
    }

    @CachePut(value = "invoiceHasLabTest")
    public List<InvoiceHasLabTest> findByInvoiceAndLabTestStatus(Invoice invoice, LabTestStatus nosample) {
        return invoiceHasLabTestDao.findByInvoiceAndLabTestStatus(invoice, nosample);
    }

    @CachePut(value = "invoiceHasLabTest")
    public InvoiceHasLabTest findByInvoiceAndLabTest(Invoice invoice, LabTest labTest) {
        return invoiceHasLabTestDao.findByInvoiceAndLabTest(invoice, labTest);
    }

    @CachePut(value = "invoiceHasLabTest")
    public List<InvoiceHasLabTest> findByInvoiceAndLabTestStatusAndLabtestDoneHere(Invoice invoice, LabTestStatus nosample, LabtestDoneHere doneHere) {
        return invoiceHasLabTestDao.findByInvoiceAndLabTestStatusAndLabTest_LabtestDoneHere(invoice, nosample, doneHere);
    }

    @CachePut(value = "invoiceHasLabTest")
    public List<InvoiceHasLabTest> findByInvoice(Invoice invoice) {
        return invoiceHasLabTestDao.findByInvoice(invoice);
    }

    @CachePut(value = "invoiceHasLabTest")
    public List<InvoiceHasLabTest> findByLabTestStateAndTestDonePlace(LabTestStatus worksheet, LabtestDoneHere yes) {
        return invoiceHasLabTestDao.findByLabTestStatusAndLabTest_LabtestDoneHere(worksheet, yes);
    }

    @CachePut(value = "invoiceHasLabTest")
    public Integer countByCreatedAt(LocalDate today) {
        return invoiceHasLabTestDao.countByCreatedAt(today);
    }

    @CachePut(value = "invoiceHasLabTest")
    public Integer countByCreatedAtIsBetween(LocalDate from, LocalDate to) {
        return invoiceHasLabTestDao.countByCreatedAtIsBetween(from, to);
    }

    @CachePut(value = "invoiceHasLabTest")
    public List<InvoiceHasLabTest> findByLabTest(LabTest labTest) {
        return invoiceHasLabTestDao.findByLabTest(labTest);
    }

    public List<LabTest> findLabTestByInvoice(Invoice invoice) {
        List<LabTest> labTests = new ArrayList<>();
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTestDao.findByInvoice(invoice))  labTests.add(invoiceHasLabTest.getLabTest());
        return labTests;
    }

    public List<InvoiceHasLabTest> findByDate(LocalDate createdAt) {
        return invoiceHasLabTestDao.findByCreatedAt(createdAt);
    }

    public List<InvoiceHasLabTest> findByDateAndUser(LocalDate date, User user) {
        return invoiceHasLabTestDao.findByCreatedAtAndUser(date, user);
    }

    public List<InvoiceHasLabTest> findByCreatedAtIsBetween(LocalDate from, LocalDate to) {
        return invoiceHasLabTestDao.findByCreatedAtIsBetween(from,to);
    }
}


