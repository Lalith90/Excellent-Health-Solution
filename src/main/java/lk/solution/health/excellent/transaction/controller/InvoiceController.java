package lk.solution.health.excellent.transaction.controller;

import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import lk.solution.health.excellent.util.service.DateTimeAgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceHasLabTestService invoiceHasLabTestService;
    private final DateTimeAgeService dateTimeAgeService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, InvoiceHasLabTestService invoiceHasLabTestService, DateTimeAgeService dateTimeAgeService) {
        this.invoiceService = invoiceService;
        this.invoiceHasLabTestService = invoiceHasLabTestService;
        this.dateTimeAgeService = dateTimeAgeService;
    }


    @RequestMapping
    public String invoicePage(Model model) {
        model.addAttribute("invoices", invoiceService.findByDate(dateTimeAgeService.getCurrentDate()));
        return "invoice/invoice";
    }

    //common method to view invoice details
    private void invoiceView(int id, Model model) {
        Invoice invoice = invoiceService.findById(id);
        model.addAttribute("paymentDetail", invoice);
        //filter medical package lab test and normal invoiced lab test
        List<LabTest> labTests = invoiceHasLabTestService.findLabTestByInvoice(invoice)
                .stream()
                .filter(labTest -> !invoice.getMedicalPackage().getLabTests().contains(labTest))
                .collect(Collectors.toList());
        model.addAttribute("labTest", labTests);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String invoiceViewFindById(@PathVariable("id") Integer id, Model model) {
        invoiceView(id, model);
        return "invoice/invoice-detail";
    }


    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removeInvoice(@PathVariable Integer id) {
        invoiceService.delete(id);
        return "redirect:/invoice";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model) {
        model.addAttribute("invoice", new Invoice());
        return "invoice/searchForm";
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public String search(@ModelAttribute Invoice invoice, Model model) {
        List<Invoice> invoices = invoiceService.search(invoice);
        int invoiceId = 0;

        if (invoices.isEmpty()) {
            return "redirect:/search";
        }
        if (invoices.size() == 1) {
            for (Invoice onlyOne : invoices) {
                invoiceId = onlyOne.getId();
            }
        } else {
            model.addAttribute("invoices", invoices);
            return "invoice/invoice";
        }
        invoiceView(invoiceId, model);
        return "invoice/invoice-detail";
    }
}
