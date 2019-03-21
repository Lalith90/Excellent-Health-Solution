package lk.solution.health.excellent.general.controller;

import lk.solution.health.excellent.common.service.DateTimeAgeService;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.resource.entity.Enum.CollectingCenterStatus;
import lk.solution.health.excellent.resource.entity.Enum.MedicalPackageStatus;
import lk.solution.health.excellent.resource.service.*;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lk.solution.health.excellent.transaction.service.DiscountRatioService;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class InvoiceHasLabTestController {
    private final InvoiceService invoiceService;
    private final LabTestService labTestService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final UserService userService;
    private final MedicalPackageService medicalPackageService;
    private final CollectingCenterService collectingCenterService;
    private final DiscountRatioService discountRatioService;
    private final DateTimeAgeService dateTimeAgeService;
    private final InvoiceHasLabTestService invoiceHasLabTestService;


    @Autowired
    public InvoiceHasLabTestController(LabTestService labTestService, PatientService patientService, DoctorService doctorService, UserService userService, MedicalPackageService medicalPackageService, InvoiceService invoiceService, CollectingCenterService collectingCenterService, DiscountRatioService discountRatioService, DateTimeAgeService dateTimeAgeService, InvoiceHasLabTestService invoiceHasLabTestService) {
        this.invoiceService = invoiceService;
        this.labTestService = labTestService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.userService = userService;
        this.medicalPackageService = medicalPackageService;
        this.collectingCenterService = collectingCenterService;
        this.discountRatioService = discountRatioService;
        this.dateTimeAgeService = dateTimeAgeService;
        this.invoiceHasLabTestService = invoiceHasLabTestService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String invoiceMakeFrom(Model model) {
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctor", doctorService.findAll());
        model.addAttribute("collectingCenter",collectingCenterService.openCollectingCenter(CollectingCenterStatus.OPEN));
        model.addAttribute("discountRatio", discountRatioService.findAll());
        model.addAttribute("labTests", labTestService.findAll().listIterator());
        model.addAttribute("medicalPackage", medicalPackageService.openMedicalPackage(MedicalPackageStatus.OPEN));
        model.addAttribute("invoiceHasLabTest", new InvoiceHasLabTest());
        model.addAttribute("addStatus", true);
        model.addAttribute("invoice", new Invoice());
        return "invoice/addInvoice";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add","/update"}, method = RequestMethod.POST)
    public String invoiceSave(@Valid @ModelAttribute Invoice invoice, BindingResult result, Model model) {
/*
        for (InvoiceHasLabTest labTest : invoice.getInvoiceHasLabTests() ){
            System.out.println(labTest.getLabTest().getId());
        }*/
//           System.out.println(invoice.getInvoiceHasLabTests());

        /*to get current user - start*/
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = userService.findByUserIdByUserName(auth.getName());
        /*to get current user - start*/
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("doctor", doctorService.findAll());
            model.addAttribute("collectingCenter",collectingCenterService.openCollectingCenter(CollectingCenterStatus.OPEN));
            model.addAttribute("discountRatio", discountRatioService.findAll());
            model.addAttribute("labTests", labTestService.findAll());
                    model.addAttribute("medicalPackage", medicalPackageService.openMedicalPackage(MedicalPackageStatus.OPEN));
            model.addAttribute("addStatus", true);
            return "/invoice/addInvoice";
        }

        invoice.setUser(userService.findById(userId));
        invoice.setCreatedAt(dateTimeAgeService.getCurrentDate());
        invoiceService.persist(invoice);
        return "redirect:/invoice";
    }

}
