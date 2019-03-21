package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.common.service.DateTimeAgeService;
import lk.solution.health.excellent.common.service.FileHandelService;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.resource.service.*;
import lk.solution.health.excellent.transaction.service.DiscountRatioService;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;

@Controller
@RequestMapping(value = "/select")
public class NewInvoiceProceessCon {
    private final InvoiceService invoiceService;
    private final UserService userService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final LabTestService labTestService;
    private final DateTimeAgeService dateTimeAgeService;
    private final CollectingCenterService collectingCenterService;
    private final DiscountRatioService discountRatioService;
    private final MedicalPackageService medicalPackageService;
    private final InvoiceHasLabTestService invoiceHasLabTestService;
    private final FileHandelService fileHandelService;
    private final ServletContext context;

    @Autowired
    public NewInvoiceProceessCon(InvoiceService invoiceService, UserService userService, PatientService patientService, DoctorService doctorService, LabTestService labTestService, DateTimeAgeService dateTimeAgeService, CollectingCenterService collectingCenterService, DiscountRatioService discountRatioService, MedicalPackageService medicalPackageService, InvoiceHasLabTestService invoiceHasLabTestService, FileHandelService fileHandelService, ServletContext context) {
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.labTestService = labTestService;
        this.dateTimeAgeService = dateTimeAgeService;
        this.collectingCenterService = collectingCenterService;
        this.discountRatioService = discountRatioService;
        this.medicalPackageService = medicalPackageService;
        this.invoiceHasLabTestService = invoiceHasLabTestService;
        this.fileHandelService = fileHandelService;
        this.context = context;
    }

    @GetMapping("/LabTest")
    public String getLabTestSelectPage(Model model){
        model.addAttribute("labTestSelect", true);
        model.addAttribute("labTests", labTestService.findAll());
        return "process/newInvoiceProcess";
    }

    @PostMapping("/newInvoiceProcess/add")
    public String getSelectedLabTest(){
        return "haha";
    }
}
