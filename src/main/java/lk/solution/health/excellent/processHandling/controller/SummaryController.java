package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.util.service.DateTimeAgeService;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.processHandling.helpingClass.SearchProcess;
import lk.solution.health.excellent.resource.entity.Patient;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.resource.service.UserService;
import lk.solution.health.excellent.transaction.entity.Enum.PaymentMethod;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lk.solution.health.excellent.transaction.entity.Refund;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import lk.solution.health.excellent.transaction.service.RefundService;
import lk.solution.health.excellent.util.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/process")
public class SummaryController {
    private final InvoiceService invoiceService;
    private final RefundService refundService;
    private final InvoiceHasLabTestService invoiceHasLabTestService;
    private final DateTimeAgeService dateTimeAgeService;
    private final UserService userService;
    private final OperatorService operatorService;

    @Autowired
    public SummaryController(InvoiceService invoiceService, RefundService refundService, InvoiceHasLabTestService invoiceHasLabTestService, DateTimeAgeService dateTimeAgeService, UserService userService, OperatorService operatorService) {
        this.invoiceService = invoiceService;
        this.refundService = refundService;
        this.invoiceHasLabTestService = invoiceHasLabTestService;
        this.dateTimeAgeService = dateTimeAgeService;
        this.userService = userService;
        this.operatorService = operatorService;
    }

    private void commonAttributeToFontEnd(Model model, User user,
                                          List<Invoice> invoices,
                                          List<InvoiceHasLabTest> invoiceHasLabTests,
                                          List<Refund> refunds,
                                          int invoicedCount
    ) {
        BigDecimal labCollection;
        BigDecimal medicalPackageCollection = BigDecimal.ZERO;
        BigDecimal totalCollection = BigDecimal.ZERO;
        BigDecimal discountedAmount = BigDecimal.ZERO;
        BigDecimal totalCash = BigDecimal.ZERO;
        BigDecimal totalCardAndCheque;
        BigDecimal totalRefund = BigDecimal.ZERO;
        BigDecimal needToDeposit;

        HashSet<Patient> patients = new HashSet<>();
        List<Invoice> cashPaymentInvoices = new ArrayList<>();

//Medical Package included List
        List<Invoice> medicalPackageIncludeInvoice = invoices.stream()
                .filter(invoice -> invoice.getMedicalPackage() != null)
                .collect(Collectors.toList());
//Medical packages amount
        for (Invoice medicalPackageInvoice : medicalPackageIncludeInvoice)
            medicalPackageCollection = operatorService.addition(medicalPackageCollection, medicalPackageInvoice.getMedicalPackage().getPrice());
//Collection
        for (Invoice totalCollectionInvoice : invoices) {
            //Total Collection
            totalCollection = operatorService.addition(totalCollection, totalCollectionInvoice.getTotalprice());
            //Total Discount Collection
            discountedAmount = operatorService.addition(discountedAmount, totalCollectionInvoice.getDiscountAmount());
            //Add patient to hash set to find invoiced patient count
            patients.add(totalCollectionInvoice.getPatient());
            //if payment cash, add invoice to cash payment method
            if (totalCollectionInvoice.getPaymentMethod() == PaymentMethod.CASH) {
                cashPaymentInvoices.add(totalCollectionInvoice);
            }
        }
// lab Collection
        labCollection = operatorService.subtraction(totalCollection, medicalPackageCollection);
// total cash collection
        for (Invoice cashPayment : cashPaymentInvoices)
            totalCash = operatorService.addition(totalCash, cashPayment.getAmount());
//card and cheque collection
        totalCardAndCheque = operatorService.subtraction(totalCollection, operatorService.addition(totalCash, discountedAmount));
//total refund collection
        for (Refund refund : refunds) totalRefund = operatorService.addition(totalRefund, refund.getAmount());
//cash to be deposit
        needToDeposit = operatorService.subtraction(totalCollection, operatorService.addition(operatorService.addition(totalRefund, totalCardAndCheque), discountedAmount));


        model.addAttribute("labCollection", labCollection.setScale(2, BigDecimal.ROUND_CEILING));
        model.addAttribute("medicalPackageCollection", medicalPackageCollection.setScale(2, BigDecimal.ROUND_CEILING));
        model.addAttribute("totalCollection", totalCollection.setScale(2, BigDecimal.ROUND_CEILING));
        model.addAttribute("discountedAmount", discountedAmount.setScale(2, BigDecimal.ROUND_CEILING));
        model.addAttribute("totalCash", totalCash.setScale(2, BigDecimal.ROUND_CEILING));
        model.addAttribute("totalCard", totalCardAndCheque.setScale(2, BigDecimal.ROUND_CEILING));
        model.addAttribute("totalRefund", totalRefund.setScale(2, BigDecimal.ROUND_CEILING));
        model.addAttribute("needToDeposit", needToDeposit.setScale(2, BigDecimal.ROUND_CEILING));
        model.addAttribute("user", user.getEmployee().getName());
        model.addAttribute("search", new SearchProcess());
        model.addAttribute("print", true);
        model.addAttribute("investigationCount", invoiceHasLabTests.size());
        model.addAttribute("medicalPackageCount", medicalPackageIncludeInvoice.size());
        model.addAttribute("patientCount", patients.size());
        model.addAttribute("invoicedCount", invoicedCount);
        model.addAttribute("date", dateTimeAgeService.getCurrentDate());
        model.addAttribute("search", new SearchProcess());
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public String cashierSummary(Model model) {
        //get who is how is available user
        User availableUser = userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
        LocalDate toDay = dateTimeAgeService.getCurrentDate();

        List<Invoice> invoices;
        List<InvoiceHasLabTest> invoiceHasLabTests;
        List<Refund> refunds;

        int patientCount;

        if (availableUser.getRole().getName().equals("ROLE_MANAGER") || availableUser.getRole().getName().equals("ROLE_MLT1")) {
            patientCount = invoiceService.countByCreatedAt(toDay);
            invoices = invoiceService.findByDate(toDay);
            invoiceHasLabTests = invoiceHasLabTestService.findByDate(toDay);
            refunds = refundService.findByDate(toDay);


            commonAttributeToFontEnd(model, availableUser, invoices, invoiceHasLabTests, refunds, patientCount);
            model.addAttribute("search", new SearchProcess());
            model.addAttribute("date", dateTimeAgeService.getCurrentDate());
            return "process/summary";
        }

        patientCount = invoiceService.countByDateAndUser(toDay, availableUser);
        //invoiceService.countByCreatedAt(toDay);
        invoices = invoiceService.findByDateAndUser(toDay, availableUser);
        //invoiceService.findByDate(toDay);
        invoiceHasLabTests = invoiceHasLabTestService.findByDateAndUser(toDay, availableUser);
        //invoiceHasLabTestService.findByDate(toDay);
        refunds = refundService.findByUserAndCreatedAt(availableUser, toDay);
        //refundService.findByDate(toDay);

        commonAttributeToFontEnd(model, availableUser, invoices, invoiceHasLabTests, refunds, patientCount);
        return "process/summary";
    }

    @RequestMapping(value = "/searchSummary", method = RequestMethod.POST)
    public String searchGivenDateRange(@ModelAttribute SearchProcess searchProcess,
                                       Model model, RedirectAttributes redirectAttributes) {
        LocalDate from = searchProcess.getStartDate();
        LocalDate to = searchProcess.getEndDate();

        if (from == null || to == null) {
            redirectAttributes.addFlashAttribute("alertStatus", true);
            return "redirect:/process/summary";
        }

        User availableUser = userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName());

        List<Invoice> invoices = invoiceService.findByCreatedAtIsBetween(from, to);
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByCreatedAtIsBetween(from, to);
        List<Refund> refunds = refundService.findByCreatedAtIsBetween(from, to);

        int patientCount = invoiceService.countByCreatedAtIsBetween(from, to);

        commonAttributeToFontEnd(model, availableUser, invoices, invoiceHasLabTests, refunds, patientCount);

        model.addAttribute("givenDate", from + " - " + to);
        return "process/summary";
    }

}
