package lk.solution.health.excellent.resource.controller;

import lk.solution.health.excellent.resource.entity.CollectingCenter;
import lk.solution.health.excellent.resource.entity.Enum.CollectingCenterStatus;
import lk.solution.health.excellent.resource.service.CollectingCenterService;
import lk.solution.health.excellent.util.service.DateTimeAgeService;
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
@RequestMapping("/collectingCenter")
public class CollectingCenterController {
    private final CollectingCenterService collectingCenterService;
    private final DateTimeAgeService dateTimeAgeService;

    @Autowired
    public CollectingCenterController(CollectingCenterService collectingCenterService, DateTimeAgeService dateTimeAgeService) {
        this.collectingCenterService = collectingCenterService;
        this.dateTimeAgeService = dateTimeAgeService;
    }



    @RequestMapping
    public String collectingCenterPage(Model model) {
        model.addAttribute("collectingCenters",collectingCenterService.findAll());
        return "collectingCenter/collectingCenter";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String collectingCenterView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("collectingCenter", collectingCenterService.findById(id));
        model.addAttribute("collectingCenterStatus", CollectingCenterStatus.values());
        return "collectingCenter/collectingCenter-detail";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editCollectingCenterFrom(@PathVariable("id") Integer id,Model model) {
        model.addAttribute("collectingCenter", collectingCenterService.findById(id));
        model.addAttribute("addStatus", false);
        model.addAttribute("collectingCenterStatus", CollectingCenterStatus.values());
        return "collectingCenter/addCollectingCenter";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String collectingCenterAddFrom(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("collectingCenterStatus", CollectingCenterStatus.values());
        model.addAttribute("collectingCenter", new CollectingCenter());
        return "collectingCenter/addCollectingCenter";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add","/update"}, method = RequestMethod.POST)
    public String addCollectingCenter(@Valid @ModelAttribute CollectingCenter collectingCenter, BindingResult result, Model model) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("addStatus", true);
            model.addAttribute("collectingCenterStatus", CollectingCenterStatus.values());
            model.addAttribute("collectingCenter", collectingCenter);
            return "collectingCenter/addCollectingCenter";
        }
        if (collectingCenter.getId()!=null){
            collectingCenterService.persist(collectingCenter);
        }
        collectingCenter.setEstablishedDate(dateTimeAgeService.getCurrentDate());
        collectingCenterService.persist(collectingCenter);
        return "redirect:/collectingCenter";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removeCollectingCenter(@PathVariable Integer id) {
        collectingCenterService.delete(id);
        return "redirect:/collectingCenter";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, CollectingCenter collectingCenter) {
        model.addAttribute("collectingCenterDetail", collectingCenterService.search(collectingCenter));
        return "collectingCenter/collectingCenter-detail";
    }
}
