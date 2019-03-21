package lk.solution.health.excellent.transaction.controller;

import lk.solution.health.excellent.common.service.DateTimeAgeService;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.resource.entity.Enum.CollectingCenterStatus;
import lk.solution.health.excellent.resource.service.*;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lk.solution.health.excellent.transaction.service.DiscountRatioService;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final LabTestService labTestService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final UserService userService;
    private final MedicalPackageService medicalPackageService;
    private final CollectingCenterService collectingCenterService;
    private final DiscountRatioService discountRatioService;
    private final DateTimeAgeService dateTimeAgeService;


    @Autowired
    public InvoiceController(LabTestService labTestService, PatientService patientService, DoctorService doctorService, UserService userService, MedicalPackageService medicalPackageService, InvoiceService invoiceService, CollectingCenterService collectingCenterService, DiscountRatioService discountRatioService, DateTimeAgeService dateTimeAgeService) {
        this.invoiceService = invoiceService;
        this.labTestService = labTestService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.userService = userService;
        this.medicalPackageService = medicalPackageService;
        this.collectingCenterService = collectingCenterService;
        this.discountRatioService = discountRatioService;
        this.dateTimeAgeService = dateTimeAgeService;
    }
    @RequestMapping
    public String invoicePage(Model model) {
        model.addAttribute("invoices", invoiceService.findAll());
        return "invoice/invoice";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String invoiceView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("invoiceDetail", invoiceService.findById(id));
        return "invoice/invoice-detail";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editInvoiceFrom(@PathVariable("id") Integer id,Model model) {
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctor", doctorService.findAll());
        model.addAttribute("collectingCenter",collectingCenterService.openCollectingCenter(CollectingCenterStatus.OPEN));
        model.addAttribute("discountRatio", discountRatioService.findAll());
        model.addAttribute("labTests", labTestService.findAll());
        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("addStatus", false);
        return "invoice/addInvoice";
    }



    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removeInvoice(@PathVariable Integer id) {
        invoiceService.delete(id);
        return "redirect:/invoice";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, Invoice invoice) {
        model.addAttribute("invoiceDetail", invoiceService.search(invoice));
        return "invoice/invoice-detail";
    }
}
