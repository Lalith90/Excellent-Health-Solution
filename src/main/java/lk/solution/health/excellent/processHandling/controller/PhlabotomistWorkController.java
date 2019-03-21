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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/phlaboto")
public class PhlabotomistWorkController {
    private final InvoiceHasLabTestService invoiceHasLabTestService;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final DateTimeAgeService dateTimeAgeService;
    private final LabTestService labTestService;
    @Autowired
    public PhlabotomistWorkController(InvoiceHasLabTestService invoiceHasLabTestService, UserService userService, InvoiceService invoiceService, DateTimeAgeService dateTimeAgeService, LabTestService labTestService) {
        this.invoiceHasLabTestService = invoiceHasLabTestService;
        this.userService = userService;
        this.invoiceService = invoiceService;
        this.dateTimeAgeService = dateTimeAgeService;
        this.labTestService = labTestService;
    }


    @RequestMapping(value = "/searchForm", method = RequestMethod.GET)
    public String searchFrom(Model model){
        model.addAttribute("addStatus", true);
        model.addAttribute("searchProcess", new SearchProcess());
        return "/phlabotoProcess/searchFrom";
    }

    public String billedPatient(Model model){
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByLabTestState(LabTestStatus.NOSAMPLE);
        // create patient list those are billed but not take sample
        List<Invoice> invoices = new ArrayList<>();
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests){
            //System.out.println();
            // add patient to array list
            invoices.add(invoiceHasLabTest.getInvoice());
        }
        //remove duplicate from he list
        List<Invoice> noDuplicateInvoice = invoices.stream()
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("invoice", noDuplicateInvoice);
        return "/phlabotoProcess/phlabotoPatientList";
    }

    @RequestMapping(value = "/searchPatient/{id}", method = RequestMethod.GET)
    public String searchPatient(@PathVariable("id") Integer id, Model model){
        Invoice invoice  = invoiceService.findById(id);
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByInvoiceAndLabTestStatus(invoice , LabTestStatus.NOSAMPLE);
        List<LabTest> labTests = new ArrayList<>();
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests){
            //System.out.println(invoiceHasLabTest.getLabTest().getId());
            labTests.add(invoiceHasLabTest.getLabTest());
        }
        model.addAttribute("invoice", invoice);
        model.addAttribute("patientAge", dateTimeAgeService.getAge(invoice.getPatient().getDateOfBirth()));
        model.addAttribute("labTests",labTests);
        model.addAttribute("searchProcess", new SearchProcess());
        return "/phlabotoProcess/patientLabTestDetails";
    }

    @RequestMapping(value = "/searchPatient",method = RequestMethod.POST)
    public String searchPatient(@ModelAttribute SearchProcess searchProcess, Model model, RedirectAttributes attributes){
       // System.out.println(searchProcess.getId());
        Invoice invoice  = invoiceService.findById(searchProcess.getId());
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByInvoiceAndLabTestStatus(invoice , LabTestStatus.NOSAMPLE);

        if (invoiceHasLabTests.isEmpty() || invoice == null){
           // System.out.println("there is no lab test without taken sample");
            attributes.addFlashAttribute("invoiceId", searchProcess.getId());
            attributes.addFlashAttribute("searchStatus",true);
            return "redirect:/phlaboto/searchForm";
        }
        List<LabTest> labTests = new ArrayList<>();
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests){
            //System.out.println(invoiceHasLabTest.getLabTest().getId());
            labTests.add(invoiceHasLabTest.getLabTest());
        }
        model.addAttribute("invoice", invoice);
        model.addAttribute("patientAge", dateTimeAgeService.getAge(invoice.getPatient().getDateOfBirth()));
        model.addAttribute("labTests",labTests);
        model.addAttribute("searchProcess", new SearchProcess());
        return "/phlabotoProcess/patientLabTestDetails";
    }
    @RequestMapping(value = "/saveSampledPatient", method = RequestMethod.POST)
    public String saveSampleTakenPatient(@ModelAttribute SearchProcess searchProcess, RedirectAttributes attributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Invoice invoice = invoiceService.findById(searchProcess.getId());
        List<LabTest> newlabTests = searchProcess.getLabTests();

        if (newlabTests.isEmpty()){
           // System.out.println("there is no lab test without taken sample");
            attributes.addFlashAttribute("invoiceId", searchProcess.getId());
            attributes.addFlashAttribute("sampleCollectStatus",true);
            return "redirect:/phlaboto/searchForm";
        }
        List<InvoiceHasLabTest> invoiceHasLabTests = new ArrayList<>();
        for (LabTest labTest : newlabTests){
           invoiceHasLabTests.add(invoiceHasLabTestService.findByInvoiceAndLabTest(invoice, labTest));
        }

        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests){
          invoiceHasLabTest.setLabTestStatus(LabTestStatus.SAMPLECOLLECT);
          invoiceHasLabTest.setSampleCollectedDateTime(dateTimeAgeService.getCurrentDateTime());
          invoiceHasLabTest.setSampleCollectingUser(userService.findByUserName(authentication.getName()));
          invoiceHasLabTestService.persist(invoiceHasLabTest);
        }

        return "redirect:/phlaboto";
    }


    @RequestMapping(value = "/repeatSamplePatientForm", method = RequestMethod.GET)
    public String repeatSamplePatientFindFrom( Model model){
        model.addAttribute("addStatus",false);
        model.addAttribute("repeatCollectStatus", true);
        return "/phlabotoProcess/searchFrom";
    }


    @RequestMapping(value = "/repeatSamplePatient", method = RequestMethod.POST)
    public String repeatSamplePatient(@ModelAttribute SearchProcess searchProcess, Model model,RedirectAttributes attributes){
        Invoice invoice = invoiceService.findById(searchProcess.getId());

        LabTest labTest = labTestService.findByCode(searchProcess.getCode());


        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findByInvoiceAndLabTest(invoice, labTest);
        System.out.println(invoiceHasLabTest.getLabTestStatus().getLabTestStatus());
        if(invoiceHasLabTest.getLabTestStatus().equals(LabTestStatus.RESULTENTER)){
            attributes.addFlashAttribute("invoiceId", invoice.getId());
            attributes.addFlashAttribute("repeatSampleStatus", true);
            return "redirect:/phlaboto/repeatSamplePatientForm";
        }
        /*List<LabTest> labTests = new ArrayList<>();
        labTests.add(labTest);*/

        model.addAttribute("invoice", invoiceHasLabTest.getInvoice());
        model.addAttribute("patientAge", dateTimeAgeService.getAge(invoice.getPatient().getDateOfBirth()));
        model.addAttribute("labTests",invoiceHasLabTest.getLabTest());
        model.addAttribute("searchProcess", new SearchProcess());
        return "/phlabotoProcess/patientLabTestDetails";
    }
}
