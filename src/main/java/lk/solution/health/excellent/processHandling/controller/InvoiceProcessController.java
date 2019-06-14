package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.general.entity.Enum.Gender;
import lk.solution.health.excellent.general.entity.Enum.Title;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.ConsultationService;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.processHandling.helpingClass.InvoiceProcess;
import lk.solution.health.excellent.resource.entity.Enum.CollectingCenterStatus;
import lk.solution.health.excellent.resource.entity.Enum.MedicalPackageStatus;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.resource.service.*;
import lk.solution.health.excellent.transaction.entity.Enum.InvoicePrintOrNot;
import lk.solution.health.excellent.transaction.entity.Enum.PaymentMethod;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lk.solution.health.excellent.transaction.service.DiscountRatioService;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import lk.solution.health.excellent.util.service.DateTimeAgeService;
import lk.solution.health.excellent.util.service.EmailService;
import lk.solution.health.excellent.util.service.ExceptionService;
import lk.solution.health.excellent.util.service.FileHandelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

@Controller
@RequestMapping(value = "/invoiceProcess")
public class InvoiceProcessController {
    private static Logger logger = LoggerFactory.getLogger(InvoiceProcessController.class);
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
    private final EmailService emailService;
    private final ExceptionService exceptionService;

    @Autowired
    public InvoiceProcessController(InvoiceService invoiceService, UserService userService, PatientService patientService,
                                    DoctorService doctorService, LabTestService labTestService, DateTimeAgeService dateTimeAgeService,
                                    CollectingCenterService collectingCenterService, DiscountRatioService discountRatioService,
                                    MedicalPackageService medicalPackageService, InvoiceHasLabTestService invoiceHasLabTestService,
                                    FileHandelService fileHandelService, ServletContext context, ConsultationService consultationService, EmailService emailService, ExceptionService exceptionService) {
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
        this.emailService = emailService;
        this.exceptionService = exceptionService;
    }

    @GetMapping
    public String invoiceHandle(Model model) {
        model.addAttribute("invoiceProcess", new InvoiceProcess());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("collectingCenters", collectingCenterService.openCollectingCenter(CollectingCenterStatus.OPEN));
        model.addAttribute("discountRatios", discountRatioService.findAll());
        model.addAttribute("labTests", labTestService.findAll());
        model.addAttribute("medicalPackages", medicalPackageService.openMedicalPackage(MedicalPackageStatus.OPEN));
        model.addAttribute("consultations", consultationService.findAll());
        model.addAttribute("lastPatient", patientService.lastPatient().getNumber());
        model.addAttribute("title", Title.values());
        model.addAttribute("gender", Gender.values());
        model.addAttribute("patientDetailsChange", false);
        return "process/invoiceProcess";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String newInvoice(@Valid @ModelAttribute InvoiceProcess invoiceProcess, BindingResult result, Model model, RedirectAttributes attributes,
                             HttpServletRequest request, HttpServletResponse response) {
        // Second value is greater than one {< 0}, Both are equal { = 0}, First value is greater {>0}
        //boolean value = invoiceProcess.getAmount().compareTo(invoiceProcess.getAmountTendered()) < 0;
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("invoicePrintOrNot", InvoicePrintOrNot.values());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("collectingCenters", collectingCenterService.openCollectingCenter(CollectingCenterStatus.OPEN));
            model.addAttribute("discountRatios", discountRatioService.findAll());
            model.addAttribute("labTests", labTestService.findAll());
            model.addAttribute("medicalPackages", medicalPackageService.openMedicalPackage(MedicalPackageStatus.OPEN));
            return "process/invoiceProcess";
        }

        //To take user
        User currentUser = userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
        /* Make invoice number with current year 000_138_484 --> start*/
        // new invoice number (1_900_000_000)
        int newInvoiceNumber;
        // if previous invoice number is null
        int previousNumber = 0;
        int newNumberFirstTwoCharacters = 0;
        LocalDate currentDate = dateTimeAgeService.getCurrentDate();
        int currentYearLastTwoNumber = Integer.parseInt(String.valueOf(currentDate.getYear()).substring(2, 4));
        if (invoiceService.findLastInvoice() != null) {
            previousNumber = invoiceService.findLastInvoice().getNumber();
            newNumberFirstTwoCharacters = Integer.parseInt(String.valueOf(invoiceService.findLastInvoice().getNumber()).substring(0, 2));

            if (currentYearLastTwoNumber == newNumberFirstTwoCharacters) {
                newInvoiceNumber = previousNumber + 1;
            } else {
                newInvoiceNumber = previousNumber + 100_000_000;
            }
        } else {
            newInvoiceNumber = Integer.parseInt(currentYearLastTwoNumber + "00000000");
        }
        /* Make invoice number with current year 1_900_000_000 --> start*/

        // to collect all selected lab test
        HashSet<LabTest> labTests = new HashSet<>();

        /*medical package and lab tests -- start*/
        // all lab test medical package and normal lab test
        if (invoiceProcess.getLabTests() != null && invoiceProcess.getMedicalPackage() != null) {
            labTests.addAll(invoiceProcess.getMedicalPackage().getLabTests());
            labTests.addAll(invoiceProcess.getLabTests());
        }
        // normal lab test
        if (invoiceProcess.getMedicalPackage() == null) {
            labTests.addAll(invoiceProcess.getLabTests());
        }
        // medical package lab test
        if (invoiceProcess.getLabTests() == null) {
            labTests.addAll(invoiceProcess.getMedicalPackage().getLabTests());
        }

        /*Patient Details verification - start*/
        //Patient already in system check some details check to re-verify
        if (invoiceProcess.getPatient().getId() == null) {
            if (patientService.findByNIC(invoiceProcess.getPatient().getNic()) != null) {
                attributes.addFlashAttribute("patientDetailsChange", true);
                return "redirect:/invoiceProcess";
            }

        }
        //find patient already in or not in system
        if (patientService.search(invoiceProcess.getPatient()).isEmpty()) {
            // new patient registered  with email and send email with his details
            if (invoiceProcess.getPatient().getEmail() != null) {
                String message = "Welcome to Excellent Health Solution \n " +
                        "Your registration number is " + invoiceProcess.getPatient().getNumber() +
                        "\nYour Details are" +
                        "\n " + invoiceProcess.getPatient().getTitle().getTitle() + " " + invoiceProcess.getPatient().getName() +
                        "\n " + invoiceProcess.getPatient().getNic() +
                        "\n " + invoiceProcess.getPatient().getDateOfBirth() +
                        "\n " + invoiceProcess.getPatient().getMobile() +
                        "\n " + invoiceProcess.getPatient().getLand() +
                        "\n\n\n\n\n Please inform us to if there is any changes on your details" +
                        "\n Kindly request keep your data up to date with us. so we can provide better service for you." +
                        "\n \n \n   Thank You" +
                        "\n Excellent Health Solution" +
                        "This is a one way communication email service \n please do not reply";
                boolean isFlag = emailService.sendPatientRegistrationEmail(invoiceProcess.getPatient().getEmail(), "Welcome to Excellent Health Solution ", message);
                invoiceProcess.getPatient().setCreatedAt(dateTimeAgeService.getCurrentDate());

            } else {
                // if patient already in system and some changed apply to his details
                invoiceProcess.getPatient().setUpdatedAt(dateTimeAgeService.getCurrentDate());
                // if update patient details / and if patient have email send patient new detals
                if (invoiceProcess.getPatient().getEmail() != null) {
                    String message = "Welcome to Excellent Health Solution \n " +
                            "Your detail is updated" +
                            "\nYour Details are" +
                            "\n " + invoiceProcess.getPatient().getTitle().getTitle() + " " + invoiceProcess.getPatient().getName() +
                            "\n " + invoiceProcess.getPatient().getNic() +
                            "\n " + invoiceProcess.getPatient().getDateOfBirth() +
                            "\n " + invoiceProcess.getPatient().getMobile() +
                            "\n " + invoiceProcess.getPatient().getLand() +
                            "\n\n\n\n\n Please inform us to if there is any changes on your details" +
                            "\n Kindly request keep your data up to date with us. so we can provide better service for you." +
                            "\n \n \n   Thank You" +
                            "\n Excellent Health Solution" +
                            "\n\n\n\n" +
                            "This is a one way communication email service \n please dont reply";
                    boolean isFlag = emailService.sendPatientRegistrationEmail(invoiceProcess.getPatient().getEmail(), "Welcome to Excellent Health Solution ", message);


                }
            }
        }


        BigDecimal totalPrice = invoiceProcess.getTotalprice();
        totalPrice.setScale(2, BigDecimal.ROUND_CEILING);
        BigDecimal backEndAmount = BigDecimal.ZERO;
        BigDecimal discountPrice = BigDecimal.ZERO;
        if (!invoiceProcess.getDiscountRatio().getAmount().equals(BigDecimal.ZERO)) {
            BigDecimal amount = totalPrice.multiply(invoiceProcess.getDiscountRatio().getAmount());
            discountPrice = amount.divide(BigDecimal.valueOf(100));
            backEndAmount = totalPrice.subtract(discountPrice);
        }
        if (backEndAmount.equals(invoiceProcess.getAmount())) {
            return "redirect:/invoiceProcess";
        }

        Invoice invoice = new Invoice();

        //Payment method is CASH
        if (invoiceProcess.getPaymentMethod() != PaymentMethod.CASH) {
            invoice.setBankName(invoiceProcess.getBankName());
            invoice.setCardNumber(exceptionService.stringToInt(invoiceProcess.getCardNumber().trim()));
        } else {
            invoice.setAmountTendered(invoiceProcess.getAmountTendered());
            invoice.setBalance(invoiceProcess.getBalance());
        }
        /*//--> If medical package has not any lab test hence we set to invoice medical package as null <--//*/
        if (invoiceProcess.getMedicalPackage().getLabTests().isEmpty()) {
            invoice.setMedicalPackage(null);
        } else {
            invoice.setMedicalPackage(invoiceProcess.getMedicalPackage());
        }
        invoice.setAmount(invoiceProcess.getAmount());
        invoice.setTotalprice(invoiceProcess.getTotalprice());
        invoice.setNumber(newInvoiceNumber);
        invoice.setPaymentMethod(invoiceProcess.getPaymentMethod());
        invoice.setRemarks(invoiceProcess.getRemarks());
        invoice.setCreatedAt(currentDate);
        invoice.setPatient(invoiceProcess.getPatient());
        invoice.setCollectingCenter(invoiceProcess.getCollectingCenter());
        invoice.setDiscountRatio(invoiceProcess.getDiscountRatio());
        invoice.setUser(currentUser);
        invoice.setDoctor(invoiceProcess.getDoctor());
        invoice.setInvoicePrintOrNot(invoiceProcess.getInvoicePrintOrNot());
        invoice.setDiscountAmount(discountPrice);
        invoice.setInvoicedAt(dateTimeAgeService.getCurrentDateTime());

//save invoice and get its details to save invoice has lab test
        invoice = invoiceService.persist(invoice);

        /*To lab test count number - start */
        int newInvoiceHasLabTestNumber;
        int lastInvoiceHasLabTestId;
        /*To lab test count number - end */

        if (invoiceHasLabTestService.findLastInvoiceHasLabTest() == null) {
            newInvoiceHasLabTestNumber = Integer.parseInt(currentYearLastTwoNumber + "00000000");
            lastInvoiceHasLabTestId = 0;
        } else {
            lastInvoiceHasLabTestId = invoiceHasLabTestService.findLastInvoiceHasLabTest().getId();
            int previousInvoiceHasLabTestNumber = invoiceHasLabTestService.findLastInvoiceHasLabTest().getNumber();

            int newLabTestCountNumberFirstTwoCharacters = Integer.parseInt(String.valueOf(previousInvoiceHasLabTestNumber).substring(0, 2));

            if (currentYearLastTwoNumber == newLabTestCountNumberFirstTwoCharacters) {
                newInvoiceHasLabTestNumber = previousInvoiceHasLabTestNumber + 1;
            } else {
                newInvoiceHasLabTestNumber = previousInvoiceHasLabTestNumber + 100_000_000;
            }
        }

        //System.out.println(newInvoiceHasLabTestNumber+" lab test number");


        // create new object and save
        InvoiceHasLabTest invoiceHasLabTest = new InvoiceHasLabTest();

        invoiceHasLabTest.setInvoice(invoice);
        invoiceHasLabTest.setLabTestStatus(LabTestStatus.NOSAMPLE);
        invoiceHasLabTest.setCreatedAt(currentDate);
//save until all lab Test array finished
        for (LabTest labtest : labTests) {
            invoiceHasLabTest.setNumber(newInvoiceHasLabTestNumber);
            invoiceHasLabTest.setId(lastInvoiceHasLabTestId + 1);
            invoiceHasLabTest.setLabTest(labtest);
            invoiceHasLabTestService.persist(invoiceHasLabTest);
            lastInvoiceHasLabTestId++;
            newInvoiceHasLabTestNumber++;
        }
//if patient not asked what printed bill nut he has email therefor bill would be send through email
        if (invoice.getInvoicePrintOrNot() == InvoicePrintOrNot.NOT && !invoice.getPatient().getEmail().isEmpty()) {
            //set all lab tests to one string
            int selectedLabTestCount = 1;
            String labTestList = "";
            for (LabTest test : invoiceProcess.getLabTests()) {
                labTestList += selectedLabTestCount + "\t" + test.getName() + "\t\t\t" + test.getPrice() + "\n";
                selectedLabTestCount++;
            }
            int selectedMedicalPackageIncludedLabTestCount = 1;
            String medicalPackageNameAndLabTest = "No Select Medical Package";
            if (invoiceProcess.getMedicalPackage() != null) {
                medicalPackageNameAndLabTest = "Medical Package Name : \t" + invoiceProcess.getMedicalPackage().getName() + "\t Price : \t" + invoiceProcess.getMedicalPackage().getPrice() + "\n \t\tIncluded Lab test list\n";
                for (LabTest labTest : invoiceProcess.getMedicalPackage().getLabTests()) {
                    medicalPackageNameAndLabTest += selectedMedicalPackageIncludedLabTestCount + "\t" + labTest.getName() + "\n";
                    selectedMedicalPackageIncludedLabTestCount++;
                }
            }
            String message = "\n \t\t\t\t\t\t SERVICE NON APPOINTMENTS - RECEIPT" +
                    "\n \t\t\t\t ----------------------------------------------------------------------------------------------------" +
                    "\n Bill No : \t\t\t\t" + invoice.getNumber() + "\t\t\t\tDate : \t\t" + invoice.getInvoicedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    "\n Patient Name : \t\t" + invoice.getPatient().getTitle().getTitle() + " " + invoice.getPatient().getName() + "\t\tMobile : \t\t" + invoice.getPatient().getMobile() +
                    "\n Referral Doctor : \t\t" + invoice.getDoctor().getTitle().getTitle() + " " + invoice.getDoctor().getName() +
                    "\n\n \t\t\t\t\t Selected Lab Tests \n" + labTestList +
                    "\n\n \t\t\t\t\tMedical Package Details  \n" + medicalPackageNameAndLabTest +
                    "\n\n\n Discount (Rs) \t\t\t  - " + discountPrice +
                    "\n Net Amount (Rs) \t\t- " + totalPrice.setScale(2, BigDecimal.ROUND_CEILING).toString() +
                    "\n\n --------------------" +
                    "\n Cashier(" + currentUser.getEmployee().getCallingName() + ")" +
                    "\n\n\n\n\n Please inform us to if there is any changes on this bill" +
                    "\n \n \n   \t\t Thank You" +
                    "\n \t Excellent Health Solution" +
                    "\n\n\n\n\nThis is a one way communication email service hence please do not reply if you need to further details regarding anything take call to hot line  " +
                    "\n\n\n\n\nWe will not responsible for reports not collected within 30 days.   ";

            boolean isFlag = emailService.sendPatientRegistrationEmail(invoiceProcess.getPatient().getEmail(), "Welcome to Excellent Health Solution ", message);
        } else {
            //to print invoice
            boolean isFlag = invoiceService.createPdf(invoice, context, request, response);
            if (isFlag) {
                String fullPath = request.getServletContext().getRealPath("/resources/report/" + invoice.getPatient().getTitle().getTitle() + "." + invoice.getPatient().getName() + "-invoice" + ".pdf");
                System.out.println(fullPath + "this is from invoice process pdf file path");
                boolean download = fileHandelService.fileDownload(fullPath, response, invoice.getPatient().getTitle().getTitle() + " " + invoice.getPatient().getName() + "invoice" + ".pdf");
                if (download) {
                    return "redirect:/invoiceProcess";
                }
            }
        }


        return "redirect:/invoiceProcess";
    }

}