package lk.solution.health.excellent.transaction.controller;

import lk.solution.health.excellent.transaction.entity.Invoice;
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

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }


    @RequestMapping
    public String invoicePage(Model model) {
        model.addAttribute("invoices", invoiceService.findAll());
        return "invoice/invoice";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String invoiceView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("paymentDetail", invoiceService.findById(id));
        return "invoice/invoice-detail";
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
