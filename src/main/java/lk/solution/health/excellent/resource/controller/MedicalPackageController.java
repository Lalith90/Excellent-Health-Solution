package lk.solution.health.excellent.resource.controller;

import lk.solution.health.excellent.util.service.DateTimeAgeService;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.resource.entity.Enum.MedicalPackageStatus;
import lk.solution.health.excellent.resource.entity.MedicalPackage;
import lk.solution.health.excellent.resource.service.MedicalPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/medicalPackage")
public class MedicalPackageController {
    private final MedicalPackageService medicalPackageService;
    private final LabTestService labTestService;
    private final DateTimeAgeService dateTimeAgeService;

    @Autowired
    public MedicalPackageController(MedicalPackageService medicalPackageService, LabTestService labTestService, DateTimeAgeService dateTimeAgeService) {
        this.medicalPackageService = medicalPackageService;
        this.labTestService = labTestService;
        this.dateTimeAgeService = dateTimeAgeService;
    }


    @RequestMapping
    public String medicalPackagePage(Model model) {
        model.addAttribute("medicalPackages", medicalPackageService.findAll());
        return "medicalPackage/medicalPackage";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String medicalPackageView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("medicalPackageStatus", MedicalPackageStatus.values());
        model.addAttribute("medicalPackageDetail", medicalPackageService.findById(id));
        return "medicalPackage/medicalPackage-detail";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editMedicalPackageFrom(@PathVariable("id") Integer id,Model model) {
        model.addAttribute("addStatus", false);
        model.addAttribute("labTests", labTestService.findAll());
        model.addAttribute("medicalPackage", medicalPackageService.findById(id));
        model.addAttribute("medicalPackageStatus", MedicalPackageStatus.values());
        return "medicalPackage/addMedicalPackage";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String medicalPackageAddFrom(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("labTests", labTestService.findAll());
        model.addAttribute("medicalPackageStatus", MedicalPackageStatus.values());
        model.addAttribute("medicalPackage", new MedicalPackage());
        return "medicalPackage/addMedicalPackage";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add","/update"}, method = RequestMethod.POST)
    public String addMedicalPackage(@Valid MedicalPackage medicalPackage, BindingResult result, Model model) {
        System.out.println(medicalPackage);
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("addStatus", true);
            model.addAttribute("medicalPackageStatus", MedicalPackageStatus.values());
            model.addAttribute("medicalPackage", new MedicalPackage());
            return "/medicalPackage/addMedicalPackage";
        }
        if (medicalPackage.getId() != null){
            List<LabTest> updatedList = medicalPackageService.findById(medicalPackage.getId()).getLabTests();
            for (LabTest labTest : medicalPackage.getLabTests()){
                updatedList.add(labTest);
            }
            medicalPackage.setCreatedDate(dateTimeAgeService.getCurrentDate());
            medicalPackage.setLabTests(updatedList);
            medicalPackageService.persist(medicalPackage);
        }
            medicalPackageService.persist(medicalPackage);

        return "redirect:/medicalPackage";
    }
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removePrint(@PathVariable Integer id) {
       medicalPackageService.delete(id);
        return "redirect:/medicalPackage";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, MedicalPackage medicalPackage) {
        model.addAttribute("medicalPackageDetail", medicalPackageService.search(medicalPackage));
        return "medicalPackage/medicalPackage-detail";
    }

}
