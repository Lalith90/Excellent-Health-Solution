package lk.solution.health.excellent.transaction.controller;

import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.resource.service.UserService;
import lk.solution.health.excellent.transaction.entity.Refund;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import lk.solution.health.excellent.transaction.service.RefundService;
import lk.solution.health.excellent.util.service.DateTimeAgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/refund")
public class RefundController {
    private final RefundService refundService;
    private final DateTimeAgeService dateTimeAgeService;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final InvoiceHasLabTestService invoiceHasLabTestService;

    @Autowired
    public RefundController(DateTimeAgeService dateTimeAgeService, UserService userService, InvoiceService invoiceService, RefundService refundService, InvoiceHasLabTestService invoiceHasLabTestService) {
        this.dateTimeAgeService = dateTimeAgeService;
        this.userService = userService;
        this.invoiceService = invoiceService;
        this.refundService = refundService;
        this.invoiceHasLabTestService = invoiceHasLabTestService;
    }


    @RequestMapping
    public String refundPage(Model model) {
        model.addAttribute("refunds", refundService.findAll());
        return "refund/refund";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String refundView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("refundDetail", refundService.findById(id));
        return "refund/refund-detail";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editRefundFrom(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("refund", refundService.findById(id));
        model.addAttribute("addStatus", false);
        return "refund/addRefund";
    }

    //common attribute collect to one place
    private void commonModel(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("invoiceError", false);
        model.addAttribute("invoice", invoiceService.findAll());
        model.addAttribute("refund", new Refund());
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String refundAddFrom(Model model) {
        commonModel(model);
        return "refund/addRefund";
    }

    //common save method
    private void commonRefundSave(Refund refund) {
        Refund ref = new Refund();
        ref.setAmount(refund.getAmount());
        ref.setCreatedAt(dateTimeAgeService.getCurrentDate());
        ref.setUser(userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()));
        ref.setReason(refund.getReason());
        ref.setInvoice(invoiceService.findByNumber(refund.getInvoice().getNumber()));
        refundService.persist(ref);
    }
    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add", "/update"}, method = RequestMethod.POST)
    public String addRefund(@Valid @ModelAttribute Refund refund, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            commonModel(model);
            return "/refund/addRefund";
        }

        boolean workSheetTakenOrNot = false;

        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTestService.findByInvoice(invoiceService.findByNumber(refund.getInvoice().getNumber()))) {
            if (invoiceHasLabTest.getLabTestStatus() != LabTestStatus.NOSAMPLE) {
                workSheetTakenOrNot = true;
            }
        }

        if (workSheetTakenOrNot) {
            model.addAttribute("addStatus", true);
            model.addAttribute("invoiceError", true);
            model.addAttribute("invoice", invoiceService.findAll());
            model.addAttribute("refund", refund);
            redirectAttributes.addFlashAttribute("invoiceNumber", refund.getInvoice().getNumber());
            return "refund/addRefund";
        }
        commonRefundSave(refund);
        return "redirect:/refund";
    }

    @RequestMapping(value = "/managerAdd", method = RequestMethod.POST)
    public String addRefund(@Valid @ModelAttribute Refund refund, BindingResult result, Model model) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            commonModel(model);
            return "/refund/addRefund";
        }
        commonRefundSave(refund);
        return "redirect:/refund";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removeRefund(@PathVariable Integer id) {
        refundService.delete(id);
        return "redirect:/refund";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, Refund refund) {
        model.addAttribute("refundDetail", refundService.search(refund));
        return "refund/refund-detail";
    }
}
