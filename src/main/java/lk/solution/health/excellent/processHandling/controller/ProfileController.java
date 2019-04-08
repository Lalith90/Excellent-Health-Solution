package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.processHandling.helpingClass.PasswordChange;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.resource.service.EmployeeService;
import lk.solution.health.excellent.resource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class ProfileController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeService  employeeService;

    @Autowired
    public ProfileController(UserService userService, PasswordEncoder passwordEncoder, EmployeeService employeeService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.employeeService = employeeService;
    }
/*Profile changes = start */
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String userProfile(Model model, Principal principal){
        Integer uId = userService.findByUserIdByUserName(principal.getName());
        model.addAttribute("addStatus", true);
        model.addAttribute("employeeDetail",userService.findById(uId).getEmployee());
        return "employee/employee-detail";
    }
    @RequestMapping(value = "/passwordChange", method = RequestMethod.GET)
    public String passwordChangeForm(Model model){
        model.addAttribute("pswChange", new PasswordChange());
        return "login/passwordChange";
    }
    @RequestMapping(value = "/user/passwordChange", method = RequestMethod.POST)
    public String passwordChange(@Valid @ModelAttribute PasswordChange passwordChange, Model model, BindingResult result){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findById(userService.findByUserIdByUserName(auth.getName()));

        if (!result.hasErrors() && passwordEncoder.matches(passwordChange.getOpsw(),user.getPassword()) && passwordChange.getNpsw().equals(passwordChange.getNrepsw())){
            user.setPassword(passwordChange.getNpsw());
            userService.persist(user);
            System.out.println(passwordChange.getNpsw());
            model.addAttribute("message", "Successfully Change Your Password");
            model.addAttribute("alert", true);
            return "fragments/alert";
        }
        return "redirect:/passwordChange";
    }

    /*Profile changes = end */

}