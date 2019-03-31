package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.common.service.DateTimeAgeService;
import lk.solution.health.excellent.common.service.FileHandelService;
import lk.solution.health.excellent.general.entity.Enum.Gender;
import lk.solution.health.excellent.general.entity.Enum.Title;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.ConsultationService;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.processHandling.helpingClass.InvoiceProcess;
import lk.solution.health.excellent.resource.entity.Enum.CollectingCenterStatus;
import lk.solution.health.excellent.resource.entity.Enum.MedicalPackageStatus;
import lk.solution.health.excellent.resource.entity.Patient;
import lk.solution.health.excellent.resource.service.*;
import lk.solution.health.excellent.transaction.entity.Enum.PaymentMethod;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InvoiceProcessController {
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
    private final ConsultationService consultationService;

    @Autowired
    public InvoiceProcessController(InvoiceService invoiceService, UserService userService, PatientService patientService,
                                    DoctorService doctorService, LabTestService labTestService, DateTimeAgeService dateTimeAgeService,
                                    CollectingCenterService collectingCenterService, DiscountRatioService discountRatioService,
                                    MedicalPackageService medicalPackageService, InvoiceHasLabTestService invoiceHasLabTestService, FileHandelService fileHandelService, ServletContext context, ConsultationService consultationService) {
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
        this.consultationService = consultationService;
    }

    @RequestMapping(value = "/invoiceProcess", method = RequestMethod.GET)
    public String invoiceHandle(Model model) {
        model.addAttribute("invoiceProcess", new InvoiceProcess());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("collectingCenters", collectingCenterService.openCollectingCenter(CollectingCenterStatus.OPEN));
        model.addAttribute("discountRatios", discountRatioService.findAll());
        model.addAttribute("labTests", labTestService.findAll());
        model.addAttribute("medicalPackages", medicalPackageService.openMedicalPackage(MedicalPackageStatus.OPEN));
        String lastPatientNumber =  patientService.lastPatient().getNumber();
        model.addAttribute("consultations", consultationService.findAll());
        String patientNumber= lastPatientNumber.replaceAll("[^0-9]+", "");
        Integer PatientNumber = Integer.parseInt(patientNumber);
        int newPatientNumber = PatientNumber+1;
        model.addAttribute("lastPatient",lastPatientNumber);
        model.addAttribute("newPatient","EHS"+ newPatientNumber);
        model.addAttribute("title", Title.values());
        model.addAttribute("gender", Gender.values());
        model.addAttribute("patient", new Patient());

        return "process/invoiceProcess";
    }

    @RequestMapping(value = "/invoiceProcess/add", method = RequestMethod.POST)
    public String newInvoice(@Valid @ModelAttribute InvoiceProcess invoiceProcess, BindingResult result, Model model,
                             HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("collectingCenters", collectingCenterService.openCollectingCenter(CollectingCenterStatus.OPEN));
            model.addAttribute("discountRatios", discountRatioService.findAll());
            model.addAttribute("labTests", labTestService.findAll());
            model.addAttribute("medicalPackages", medicalPackageService.openMedicalPackage(MedicalPackageStatus.OPEN));
            return "process/invoiceProcess";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
// create new invoice
        Invoice invoice = new Invoice();
        invoice.setPaymentMethod(invoiceProcess.getPaymentMethod());
        invoice.setTotalprice(invoiceProcess.getTotalprice());
        invoice.setAmount(invoiceProcess.getAmount());
        invoice.setBankName(invoiceProcess.getBankName());
        invoice.setCardNumber(invoiceProcess.getCardNumber());
        invoice.setCreatedAt(dateTimeAgeService.getCurrentDate());
        invoice.setPatient(invoiceProcess.getPatient());
        invoice.setCollectingCenter(invoiceProcess.getCollectingCenter());
        invoice.setDiscountRatio(invoiceProcess.getDiscountRatio());
        invoice.setUser(userService.findByUserName(authentication.getName()));
        // If medical package is there can not add remarks for the relevant invoice
        if (invoiceProcess.getMedicalPackage() == null){
            invoice.setRemarks(invoiceProcess.getRemarks());
        }else{
            // if has medical package remarks column set medical package word to find medical package amount
            invoice.setRemarks("MEDICAL_PACKAGE  "+ invoiceProcess.getRemarks());
        }

        invoice.setMedicalPackage(invoiceProcess.getMedicalPackage());
        invoice.setDoctor(invoiceProcess.getDoctor());

        InvoiceHasLabTest invoiceHasLabTest = new InvoiceHasLabTest();

        // get lab test separately from medical package and lab test
        List<LabTest> labtestFromInvoiceProcess = invoiceProcess.getLabTests();
        List<LabTest> medicalPackgeLabTest = medicalPackageService.findById(invoiceProcess.getMedicalPackage().getId()).getLabTests();

        //Create new array list for lab test
        List<LabTest> labTests = new ArrayList<>();
        // to add all lab test together
        if (medicalPackgeLabTest != null) {
            for (LabTest labTest : medicalPackgeLabTest) {
                labTests.add(labTest);
            }
        }
        // to add all labtest for lab test
        if (labtestFromInvoiceProcess != null) {
            for (LabTest labTest : labtestFromInvoiceProcess) {
                labTests.add(labTest);
            }
        }
        //save invoice and get saved invoice
        Invoice invoice1 = invoiceService.persist(invoice);
        invoiceHasLabTest.setInvoice(invoice1);

        // get last record from invoice has lab test table
        Integer invoiceHasLabTestLastId = invoiceHasLabTestService.lastInvoiceHasLabTest().getId() + 1;

        for (LabTest labtest : labTests) {
            //System.out.println(labtest.getId());
            invoiceHasLabTest.setId(invoiceHasLabTestLastId);
            invoiceHasLabTest.setLabTest(labtest);
            invoiceHasLabTest.setCreatedAt(dateTimeAgeService.getCurrentDate());
            invoiceHasLabTest.setUser(userService.findByUserName(authentication.getName()));
            invoiceHasLabTestService.persist(invoiceHasLabTest);
            invoiceHasLabTestLastId++;
        }

        //to print invoice
        boolean isFlag = invoiceService.createPdf(invoice1, context, request, response);
        System.out.println(isFlag);
        if (isFlag) {
            String fullPath = request.getServletContext().getRealPath("/resources/report/" + invoiceHasLabTest.getInvoice().getPatient().getName() + ".pdf");
            boolean download = fileHandelService.filedownload(fullPath, response, invoiceHasLabTest.getInvoice().getPatient().getName() + ".pdf");
            if (download){
                return "redirect:/invoiceProcess";
            }
            String message = "Invoice may not be printed because of internal error. patient invoice is saved. Hence please send to sample collecting place she/her can give sample " +
                    "Invoice number is"+invoice1.getId()+"\n Amount is "+invoice1.getAmount();
            redirectAttributes.addFlashAttribute("alertStatus",false);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/invoiceProcess";
        }

        return "redirect:/invoiceProcess";
    }

  /*  @RequestMapping(value = "/cashier/invoiceLabFrom", method = RequestMethod.GET)
    public  String invoiceFrom(Model model){
        model.addAttribute("labTestFinder", true);
        model.addAttribute("labTests", labTestService.findAll());
        model.addAttribute("invoiceProcess", new InvoiceProcess());
        return "invoice/invoiceFrom";
    }

    @RequestMapping(value = "/cashier/invoiceLabFrom", method = RequestMethod.POST)
    public  String invoicedLabTest(@ModelAttribute InvoiceProcess invoiceProcess,Model model, RedirectAttributes redirectAttributes){
        if (invoiceProcess.getLabTests().isEmpty()){
            redirectAttributes.addFlashAttribute("labTestAlert",true);
            redirectAttributes.addFlashAttribute("alertStatus", true);
            redirectAttributes.addFlashAttribute("message", "Please select at least one lab test");
            model.addAttribute("labTestFinder", true);
            model.addAttribute("labTests", labTestService.findAll());
            model.addAttribute("invoiceProcess", new InvoiceProcess());
            return "redirect:/cashier/invoiceLabFrom";
        }

        List<BigDecimal> totalPrice = new ArrayList<>();

        for (LabTest labTest : invoiceProcess.getLabTests()){
            totalPrice.add(labTest.getPrice());
        }

        BigDecimal sumTotalPrice = BigDecimal.ZERO;
        if (!totalPrice.isEmpty()){
            sumTotalPrice = totalPrice.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        // open information from add display selected lab test
        redirectAttributes.addFlashAttribute("information", true);
        model.addAttribute("totalPrice", sumTotalPrice);
        model.addAttribute("invoiceInformation", true);
        model.addAttribute("labTests",invoiceProcess.getLabTests());
        model.addAttribute("invoiceProcess", new InvoiceProcess());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("collectingCenters", collectingCenterService.openCollectingCenter(CollectingCenterStatus.OPEN));
        model.addAttribute("discountRatios", discountRatioService.findAll());
        model.addAttribute("labTests", labTestService.findAll());
        return "invoice/invoiceFrom";
    }
    // medical Package add to invoice
    @RequestMapping(value = "/cashier/medicalPackageFrom", method = RequestMethod.GET)
    public String medicalPackage(Model model){
        model.addAttribute("medicalPackages", medicalPackageService.openMedicalPackage(MedicalPackageStatus.OPEN));
        model.addAttribute("medicalPackage",new SearchProcess());
        model.addAttribute("medicalPackageFinder", true);
        return "invoice/invoiceFrom";
    }
    @RequestMapping(value = "/cashier/addMedicalPackageFrom", method = RequestMethod.POST)
    public String showMedicalPackage(@ModelAttribute SearchProcess searchProcess, Model model,RedirectAttributes redirectAttributes){

        if (searchProcess.getId() == null){
            redirectAttributes.addFlashAttribute("labTestAlert",true);
            redirectAttributes.addFlashAttribute("alertStatus", true);
            redirectAttributes.addFlashAttribute("message", "Please select medical package to see details.");
            model.addAttribute("medicalPackages", medicalPackageService.openMedicalPackage(MedicalPackageStatus.OPEN));
            model.addAttribute("medicalPackage",new SearchProcess());
            model.addAttribute("medicalPackageFinder", true);

            return "redirect:/cashier/medicalPackageFrom";
        }
        model.addAttribute("medicalPackageDetail", medicalPackageService.findById(searchProcess.getId()));
        model.addAttribute("medicalPackages", medicalPackageService.openMedicalPackage(MedicalPackageStatus.OPEN));
        model.addAttribute("medicalPackage",new SearchProcess());
        model.addAttribute("medicalPackageFinder", true);
        model.addAttribute("medicalPackageDetails", true);
        model.addAttribute("buttonStatus", true);
        return "invoice/invoiceFrom";
    }

    @RequestMapping(value = "/cashier/medicalPackageAdd/{id}", method = RequestMethod.GET)
    public String medicalPackageAdd(@PathVariable("id") Integer id, Model model){
        // open information from add display selected medical Package
        //model.addAttribute("medicalPackages", medicalPackageService.findById(id));
        //model.addAttribute("medicalPackageDetails", true);
        model.addAttribute("invoiceProcess", new InvoiceProcess());
        model.addAttribute("invoiceInformation", true);
        model.addAttribute("buttonStatus", false);
        model.addAttribute("totalPrice",medicalPackageService.findById(id).getPrice());
        model.addAttribute("invoiceProcess", new InvoiceProcess());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("collectingCenters", collectingCenterService.openCollectingCenter(CollectingCenterStatus.OPEN));
        model.addAttribute("discountRatios", discountRatioService.findAll());
        model.addAttribute("labTests", labTestService.findAll());

        System.out.println(id);
        return "invoice/invoiceFrom";
    }

    @RequestMapping(value ={"cashier/saveInvoice","/cashier/medicalPackageAdd/saveInvoice"}, method = RequestMethod.POST)
    public String invoiceInformation(@ModelAttribute InvoiceProcess invoiceProcess, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("information", true);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        *//*System.out.println("nic");
        System.out.println(invoiceProcess.getPatient().getNic());*//*
// create new invoice
        Invoice invoice = new Invoice();
        invoice.setPaymentMethod(invoiceProcess.getPaymentMethod());
        invoice.setTotalprice(invoiceProcess.getTotalprice());
        invoice.setAmount(invoiceProcess.getAmount());
        invoice.setBankName(invoiceProcess.getBankName());
        invoice.setCardNumber(invoiceProcess.getCardNumber());
        invoice.setRemarks(invoiceProcess.getRemarks());
        invoice.setCreatedAt(dateTimeAgeService.getCurrentDate());

        if (invoiceProcess.getPatient().getNic().isEmpty() || invoiceProcess.getPatient().getNumber().isEmpty() || invoiceProcess.getPatient() !=null){
                    if (!invoiceProcess.getPatient().getNic().isEmpty()){
                        Patient patient =patientService.findByNIC(invoiceProcess.getPatient().getNic());
                        invoiceProcess.setPatient(patient);
                        invoice.setPatient(invoiceProcess.getPatient());
                    }if (invoiceProcess.getPatient().getNumber().isEmpty()){
                Patient patient =patientService.findByNIC(invoiceProcess.getPatient().getNumber());
                invoiceProcess.setPatient(patient);
                invoice.setPatient(invoiceProcess.getPatient());
            }else {
                        invoice.setPatient(invoiceProcess.getPatient());
            }
        }
        else{
        String message = "There is no patient on your selected NIC or Registration Code";
        redirectAttributes.addFlashAttribute("alertStatus",true);
        redirectAttributes.addFlashAttribute("message", message);
        return "invoice/invoiceFrom";
        }

        invoice.setCollectingCenter(invoiceProcess.getCollectingCenter());

        invoice.setDiscountRatio(invoiceProcess.getDiscountRatio());

        invoice.setUser(userService.findByUserName(authentication.getName()));
        invoice.setMedicalPackage(invoiceProcess.getMedicalPackage());

        invoice.setDoctor(invoiceProcess.getDoctor());

        //save invoice and get it's id
        //   Integer invoiceId = invoiceService.persist(invoice).getId();
        // create new invoice has lab test
        InvoiceHasLabTest invoiceHasLabTest = new InvoiceHasLabTest();

        System.out.println("medical PAckage");
        System.out.println(invoiceProcess.getMedicalPackage());

        // get lab test separately from medical package and lab test
        List<LabTest> labtestFromInvoiceProcess = invoiceProcess.getLabTests();
        List<LabTest> medicalPackgeLabTest = null;
        if (invoiceProcess.getMedicalPackage() !=null) {
            medicalPackgeLabTest = medicalPackageService.findById(invoiceProcess.getMedicalPackage().getId()).getLabTests();
        }
        //Create new array list for lab test
        List<LabTest> labTests = new ArrayList<>();
        // to add all lab test together
        if (medicalPackgeLabTest != null) {
            for (LabTest labTest : medicalPackgeLabTest) {
                labTests.add(labTest);
            }
        }
        if (labtestFromInvoiceProcess != null) {
            for (LabTest labTest : labtestFromInvoiceProcess) {
                labTests.add(labTest);
            }
        }
        System.out.println("save invoice");
        Integer invoiceId = invoiceService.persist(invoice).getId();
        Invoice invoice1 = invoiceService.findById(invoiceId);
        invoiceHasLabTest.setInvoice(invoice1);

        // get last record from invoice has lab test table
        Integer invoiceHasLabTestLastId = invoiceHasLabTestService.lastInvoiceHasLabTest().getId() + 1;
        for (LabTest labtest : labTests) {
            //System.out.println(labtest.getId());
            invoiceHasLabTest.setId(invoiceHasLabTestLastId);
            invoiceHasLabTest.setLabTest(labtest);
            invoiceHasLabTest.setCreatedAt(dateTimeAgeService.getCurrentDate());
            invoiceHasLabTest.setUser(userService.findByUserName(authentication.getName()));
            invoiceHasLabTestService.persist(invoiceHasLabTest);
            invoiceHasLabTestLastId++;
        }

       *//* //to print invoice
        boolean isFlag = invoiceService.createPdf(invoice1, context, request, response);
        System.out.println(isFlag);
        if (isFlag) {
            String fullPath = request.getServletContext().getRealPath("/resources/report/" + invoiceHasLabTest.getInvoice().getPatient().getName() + ".pdf");
            boolean download = fileHandelService.filedownload(fullPath, response, invoiceHasLabTest.getInvoice().getPatient().getName() + ".pdf");
            if (download){
                return "redirect:/cashier/invoiceLabFrom";
            }
            String message = "Invoice may not be printed because of internal error. patient invoice is saved. Hence please send to sample collecting place she/her can give sample " +
                    "Invoice number is"+invoice1.getId()+"\n Amount is "+invoice1.getAmount();
            redirectAttributes.addFlashAttribute("alertStatus",false);
            redirectAttributes.addFlashAttribute("message", message);
            return "/cashier/invoiceLabFrom";
        }*//*

        return "/cashier/invoiceLabFrom";
    }
*/
}