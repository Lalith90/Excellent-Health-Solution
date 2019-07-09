package lk.solution.health.excellent.general.controller;

import lk.solution.health.excellent.general.entity.Consultation;
import lk.solution.health.excellent.general.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


@RequestMapping("/consultation")
public class ConsultationController {
    private final ConsultationService consultationService;

    @Autowired
    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @RequestMapping
    public String consultationPage(Model model) {
        model.addAttribute("consultations", consultationService.findAll());
        return "consultation/consultation";
    }

/*    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String consultationView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("consultationDetail", consultationService.findById(id));
        return "consultation/consultation-detail";
    }*/

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editConsultationFrom(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("consultation", consultationService.findById(id));
        model.addAttribute("addStatus", false);
        return "consultation/addConsultation";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String consultationAddFrom(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("consultation", new Consultation());
        return "consultation/addConsultation";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add", "/update"}, method = RequestMethod.POST)
    public String addConsultation(@Valid @ModelAttribute Consultation consultation, BindingResult result, Model model) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("addStatus", false);
            model.addAttribute("consultation", consultation);
            return "consultation/addConsultation";
        }
        consultationService.persist(consultation);
        return "redirect:/consultation";
    }


    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removeConsultation(@PathVariable Integer id) {
        consultationService.delete(id);
        return "redirect:/consultation";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, Consultation consultation) {
        model.addAttribute("consultationDetail", consultationService.search(consultation));
        return "consultation/consultation-detail";
    }

}
