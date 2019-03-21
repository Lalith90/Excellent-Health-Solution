package lk.solution.health.excellent.transaction.controller;

import lk.solution.health.excellent.common.service.DateTimeAgeService;
import lk.solution.health.excellent.resource.service.UserService;
import lk.solution.health.excellent.transaction.entity.Refund;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import lk.solution.health.excellent.transaction.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("/refund")
public class RefundController {
    private final RefundService refundService;
    private final DateTimeAgeService dateTimeAgeService;
    private final UserService userService;
    private final InvoiceService invoiceService;

    @Autowired
    public RefundController(DateTimeAgeService dateTimeAgeService, UserService userService, InvoiceService invoiceService, RefundService refundService) {
        this.dateTimeAgeService = dateTimeAgeService;
        this.userService = userService;
        this.invoiceService = invoiceService;
        this.refundService = refundService;
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
    public String editRefundFrom(@PathVariable("id") Integer id,Model model) {
        model.addAttribute("refund", refundService.findById(id));
        model.addAttribute("addStatus", false);
        return "refund/addRefund";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String refundAddFrom(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("invoice", invoiceService.findAll());
        model.addAttribute("refund", new Refund());
        return "refund/addRefund";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add","/update"}, method = RequestMethod.POST)
    public String addRefund(@Valid @ModelAttribute Refund refund, BindingResult result, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(refund);
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("addStatus", true);
            model.addAttribute("invoice",invoiceService.findAll());
            model.addAttribute("refund", refund);
            return "/refund/addRefund";
        }
        //need to check

        refund.setInvoice(invoiceService.findById(refund.getInvoice().getId()));
        refund.setUser(userService.findByUserName(auth.getName()));
        refund.setCreatedAt(dateTimeAgeService.getCurrentDate());
        refundService.persist(refund);
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
