package lk.solution.health.excellent.lab.controller;

import lk.solution.health.excellent.lab.entity.Enum.ParameterHeader;
import lk.solution.health.excellent.lab.entity.LabTestParameter;
import lk.solution.health.excellent.lab.service.LabTestParameterService;
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
@RequestMapping("/labTestParameter")
public class LabTestParameterController {
    private final LabTestParameterService labTestParameterService;

    @Autowired
    public LabTestParameterController(LabTestParameterService labTestParameterService) {
        this.labTestParameterService = labTestParameterService;
    }


    @RequestMapping
    public String labTestParameterPage(Model model) {
        model.addAttribute("labTestParameters", labTestParameterService.findAll());
        return "labTestParameter/labTestParameter";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String labTestParameterView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("labTestParameterDetail", labTestParameterService.findById(id));
        return "labTestParameter/labTestParamater-detail";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editLabTestParameterFrom(@PathVariable("id") Integer id,Model model) {
        model.addAttribute("labTestParameter", labTestParameterService.findById(id));
        model.addAttribute("parameterHeader", ParameterHeader.values());
        model.addAttribute("addStatus", false);
        return "labTestParameter/addLabTestParameter";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String labTestParameterAddFrom(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("parameterHeader", ParameterHeader.values());
        model.addAttribute("labTestParameter", new LabTestParameter());
        return "labTestParameter/addLabTestParameter";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add","/update"}, method = RequestMethod.POST)
    public String addLabTestParameter(@Valid @ModelAttribute LabTestParameter labTestParameter, BindingResult result, Model model) {

        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("addStatus", true);
            model.addAttribute("parameterHeader", ParameterHeader.values());
            model.addAttribute("labTestParameter", labTestParameter);
            return "/labTestParameter/addLabTestParameter";
        }
        labTestParameterService.persist(labTestParameter);
        return "redirect:/labTestParameter";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removeLabTestParameter(@PathVariable Integer id) {
        labTestParameterService.delete(id);
        return "redirect:/labTestParameter";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, LabTestParameter labTestParameter) {
        model.addAttribute("labTestParameterDetail", labTestParameterService.search(labTestParameter));
        return "labTestParameter/labTestParameter-detail";
    }
}
