package lk.solution.health.excellent.resource.controller;

import lk.solution.health.excellent.resource.entity.Role;
import lk.solution.health.excellent.resource.service.RoleService;
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
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @RequestMapping
    public String rolePage(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "role/role";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String roleView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("roleDetail", roleService.findById(id));
        return "role/role-detail";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editRoleFrom(@PathVariable("id") Integer id,Model model) {
        model.addAttribute("role", roleService.findById(id));
        model.addAttribute("addStatus", false);
        return "role/addRole";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String roleAddFrom(Model model) {
        model.addAttribute("addStatus", true);
        model.addAttribute("role", new Role());
        return "role/addRole";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add","/update"}, method = RequestMethod.POST)
    public String addRole(@Valid @ModelAttribute Role role, BindingResult result, Model model) {
          if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            model.addAttribute("addStatus", true);
            model.addAttribute("role", role);
            return "/role/addRole";
        }
        roleService.persist(role);
        return "redirect:/role";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removeRole(@PathVariable Integer id) {
        roleService.delete(id);
        return "redirect:/role";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, Role role) {
        model.addAttribute("roleDetail", roleService.search(role));
        return "role/role-detail";
    }
}
