package lk.solution.health.excellent.transaction.controller;


import lk.solution.health.excellent.transaction.entity.DiscountRatio;
import lk.solution.health.excellent.transaction.service.DiscountRatioService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/discountRatio")
public class DiscountRatioController {
    private final DiscountRatioService discountRatioService;

    @Autowired
    public DiscountRatioController(DiscountRatioService discountRatioService) {
        this.discountRatioService = discountRatioService;
    }


    @RequestMapping
    public String discountRatioPage(Model model) {
        model.addAttribute("discountRatios", discountRatioService.findAll());
        return "discountRatio/discountRatio";
    }
/* not use this method
-------------------------------------------
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String discountRatioView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("discountRatioDetail", discountRatioService.findById(id));
        return "discountRatio/discountRatio-detail";
    }*/

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editDiscountRatioFrom(@PathVariable("id") Integer id,Model model) {
        model.addAttribute("discountRatio", discountRatioService.findById(id));
        model.addAttribute("addStatus", false);
        return "discountRatio/addDiscountRatio";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String discountRatioAddFrom(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("discountRatio", new DiscountRatio());
        return "discountRatio/addDiscountRatio";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add","/update"}, method = RequestMethod.POST)
    public String addDiscountRatio(@Valid @ModelAttribute DiscountRatio discountRatio, BindingResult result, Model model) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("addStatus", false);
            model.addAttribute("discountRatio", discountRatio);
            return "/discountRatio/addDiscountRatio";
        }
        discountRatioService.persist(discountRatio);
        return "redirect:/discountRatio";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removeDiscountRatio(@PathVariable Integer id) {
        discountRatioService.delete(id);
        return "redirect:/discountRatio";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, DiscountRatio discountRatio) {
        model.addAttribute("discountRatioDetail", discountRatioService.search(discountRatio));
        return "discountRatio/discountRatio-detail";
    }
}
