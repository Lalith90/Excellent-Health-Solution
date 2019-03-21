package lk.solution.health.excellent.lab.controller;

import lk.solution.health.excellent.lab.entity.Enum.Department;
import lk.solution.health.excellent.lab.entity.Enum.LabtestDoneHere;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.service.LabTestParameterService;
import lk.solution.health.excellent.lab.service.LabTestService;
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
import java.util.List;

@Controller
@RequestMapping("/labTest")
public class LabTestController {
    private final LabTestService labTestService;
    private final LabTestParameterService labTestParameterService;

    @Autowired
    public LabTestController(LabTestService labTestService, LabTestParameterService labTestParameterService) {
        this.labTestService = labTestService;
        this.labTestParameterService = labTestParameterService;
    }

    @RequestMapping
    public String laboratoryTestPage(Model model) {
        List<LabTest> labTests = labTestService.findAll();
        model.addAttribute("labTests", labTests);
        return "labTest/labTest";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String laboratoryTestView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("labTestDetail", labTestService.findById(id));
        return "labTest/labTest-detail";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editLabTestFrom(@PathVariable("id") Integer id,Model model) {
        model.addAttribute("labTest", labTestService.findById(id));
        model.addAttribute("department", Department.values());
        model.addAttribute("labTestDoneHere", LabtestDoneHere.values());
        model.addAttribute("addStatus", false);
        model.addAttribute("labTestParameters", labTestParameterService.findAll());
        return "labTest/addLabTest";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String laboratoryTestAddFrom(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("labTest", new LabTest());
        model.addAttribute("labTestDoneHere", LabtestDoneHere.values());
        model.addAttribute("department", Department.values());
        model.addAttribute("labTestParameters", labTestParameterService.findAll());
        return "labTest/addLabTest";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add","/update"}, method = RequestMethod.POST)
    public String addLabTest(@Valid @ModelAttribute LabTest labTest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("addStatus", false);
            model.addAttribute("labTestDoneHere", LabtestDoneHere.values());
            model.addAttribute("department", Department.values());
            model.addAttribute("labTestParameters", labTestParameterService.findAll());
            return "/labTest/addLabTest";
        }
    if(labTest.getId() != null){
      labTestService.persist(labTest);
    }
        labTestService.persist(labTest);
        return "redirect:/labTest";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removeLabTest(@PathVariable Integer id) {
        labTestService.delete(id);
        return "redirect:/labTest";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, LabTest labTest) {
        model.addAttribute("labTestDetail", labTestService.search(labTest));
        return "labTest/labTest-detail";
    }
}
