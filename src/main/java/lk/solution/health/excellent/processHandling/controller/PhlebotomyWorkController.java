package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.common.service.DateTimeAgeService;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.processHandling.helpingClass.SearchProcess;
import lk.solution.health.excellent.resource.service.UserService;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/phlaboto")
public class PhlebotomyWorkController {
    private final InvoiceHasLabTestService invoiceHasLabTestService;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final DateTimeAgeService dateTimeAgeService;
    private final LabTestService labTestService;

    @Autowired
    public PhlebotomyWorkController(InvoiceHasLabTestService invoiceHasLabTestService, UserService userService, InvoiceService invoiceService, DateTimeAgeService dateTimeAgeService, LabTestService labTestService) {
        this.invoiceHasLabTestService = invoiceHasLabTestService;
        this.userService = userService;
        this.invoiceService = invoiceService;
        this.dateTimeAgeService = dateTimeAgeService;
        this.labTestService = labTestService;
    }

    @GetMapping
    public String billedPatient(Model model) {
        // create patient list those are billed but not take sample
        HashSet<Invoice> invoices = new HashSet<>();

        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTestService.findByLabTestState(LabTestStatus.NOSAMPLE)) {
            invoices.add(invoiceHasLabTest.getInvoice());
        }
        model.addAttribute("invoices", invoices);
        return "phlebotomyProcess/phlebotomyPatientList";
    }

    @RequestMapping(value = "/searchForm", method = RequestMethod.GET)
    public String searchFrom(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("searchProcess", new SearchProcess());
        return "phlebotomyProcess/searchFrom";
    }

    private void commonMethodPatientSearch(Model model, Invoice invoice, List<InvoiceHasLabTest> invoiceHasLabTests) {
        List<LabTest> labTests = new ArrayList<>();
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests) {
            labTests.add(invoiceHasLabTest.getLabTest());
        }
        model.addAttribute("invoice", invoice);
        model.addAttribute("patientAge", dateTimeAgeService.getAge(invoice.getPatient().getDateOfBirth()));
        model.addAttribute("labTests", labTests);
        model.addAttribute("searchProcess", new SearchProcess());

    }

    @RequestMapping(value = "/searchPatient/{id}", method = RequestMethod.GET)
    public String searchPatient(@PathVariable("id") int id, Model model, RedirectAttributes attributes, SearchProcess searchProcess) {
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByInvoiceAndLabTestStatus(invoiceService.findById(id), LabTestStatus.NOSAMPLE);
        if (invoiceHasLabTests.isEmpty()) {
            System.out.println("Not come here ");
            attributes.addFlashAttribute("invoiceId", searchProcess.getNumber());
            attributes.addFlashAttribute("searchStatus", true);
            return "redirect:phlebotomyProcess/searchForm";
        }
        commonMethodPatientSearch(model, invoiceService.findById(id), invoiceHasLabTests);
        return "phlebotomyProcess/patientLabTestDetails";
    }

    @RequestMapping(value = "/saveSampledPatient", method = RequestMethod.POST)
    public String saveSampleTakenPatient(@ModelAttribute SearchProcess searchProcess, RedirectAttributes attributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Invoice invoice = invoiceService.findByNumber(Integer.parseInt(searchProcess.getNumber()));
        List<LabTest> newLabTests = searchProcess.getLabTests();

        if (newLabTests.isEmpty()) {
             System.out.println("there is no lab test without taken sample");
            attributes.addFlashAttribute("invoiceId", searchProcess.getId());
            attributes.addFlashAttribute("sampleCollectStatus", true);
            return "redirect:phlaboto/searchForm";
        }
        List<InvoiceHasLabTest> invoiceHasLabTests = new ArrayList<>();
        for (LabTest labTest : newLabTests) {
            invoiceHasLabTests.add(invoiceHasLabTestService.findByInvoiceAndLabTest(invoice, labTest));
        }


        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests) {
            invoiceHasLabTest.setLabTestStatus(LabTestStatus.SAMPLECOLLECT);
            invoiceHasLabTest.setSampleCollectedDateTime(dateTimeAgeService.getCurrentDateTime());
            invoiceHasLabTest.setSampleCollectingUser(userService.findByUserName(authentication.getName()));
            invoiceHasLabTestService.persist(invoiceHasLabTest);
        }

        return "redirect:phlaboto";
    }

    @RequestMapping(value = "/searchPatient", method = RequestMethod.POST)
    public String searchPatient(@ModelAttribute SearchProcess searchProcess, Model model, RedirectAttributes attributes) {
        Invoice invoice = invoiceService.findByNumber(Integer.parseInt(searchProcess.getNumber()));
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByInvoiceAndLabTestStatus(invoice, LabTestStatus.NOSAMPLE);
        if (invoiceHasLabTests.isEmpty() || invoice == null) {
            attributes.addFlashAttribute("invoiceId", searchProcess.getNumber());
            attributes.addFlashAttribute("searchStatus", true);
            return "redirect:phlebotomyProcess/searchForm";
        }
        commonMethodPatientSearch(model, invoice, invoiceHasLabTests);
        return "phlebotomyProcess/patientLabTestDetails";
    }

    //TODO
    /*javax.persistence.NonUniqueResultException: query did not return a unique result: 2
     *
     * other thing ned to javascript validation to sample taken form validation
     *
     *
     * Lab test find is not work
     *
     * repeat sample
     * */


    @RequestMapping(value = "/repeatSamplePatientForm", method = RequestMethod.GET)
    public String repeatSamplePatientFindFrom(Model model) {
        model.addAttribute("addStatus", false);
        model.addAttribute("repeatCollectStatus", true);
        return "phlebotomyProcess/searchFrom";
    }


    @RequestMapping(value = "/repeatSamplePatient", method = RequestMethod.POST)
    public String repeatSamplePatient(@ModelAttribute SearchProcess searchProcess, Model model, RedirectAttributes attributes) {
        Invoice invoice = invoiceService.findByNumber(Integer.parseInt(searchProcess.getNumber()));

        LabTest labTest = labTestService.findByCode(searchProcess.getCode());


        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findByInvoiceAndLabTest(invoice, labTest);
        System.out.println(invoiceHasLabTest.getLabTestStatus().getLabTestStatus());
        if (invoiceHasLabTest.getLabTestStatus().equals(LabTestStatus.RESULTENTER)) {
            attributes.addFlashAttribute("invoiceId", invoice.getId());
            attributes.addFlashAttribute("repeatSampleStatus", true);
            return "redirect:phlaboto/repeatSamplePatientForm";
        }
        /*List<LabTest> labTests = new ArrayList<>();
        labTests.add(labTest);*/

        model.addAttribute("invoice", invoiceHasLabTest.getInvoice());
        model.addAttribute("patientAge", dateTimeAgeService.getAge(invoice.getPatient().getDateOfBirth()));
        model.addAttribute("labTests", invoiceHasLabTest.getLabTest());
        model.addAttribute("searchProcess", new SearchProcess());
        return "phlebotomyProcess/patientLabTestDetails";
    }
}
