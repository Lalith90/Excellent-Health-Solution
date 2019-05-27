package lk.solution.health.excellent.resource.controller;

import lk.solution.health.excellent.common.service.DateTimeAgeService;
import lk.solution.health.excellent.common.service.EmailService;
import lk.solution.health.excellent.general.entity.Enum.Gender;
import lk.solution.health.excellent.general.entity.Enum.Title;
import lk.solution.health.excellent.resource.entity.Patient;
import lk.solution.health.excellent.resource.service.PatientService;
import lk.solution.health.excellent.resource.service.UserService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {
    private final PatientService patientService;
    private final DateTimeAgeService dateTimeAgeService;
    private final UserService userService;
    private final EmailService emailService;


    @Autowired
    public PatientController(PatientService patientService, DateTimeAgeService dateTimeAgeService, UserService userService, EmailService emailService) {
        this.patientService = patientService;
        this.dateTimeAgeService = dateTimeAgeService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @RequestMapping
    public String patientPage(Model model) {
        model.addAttribute("patients",  patientService.findAll());
        return "patient/patient";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String patientView(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("patientDetail", patientService.findById(id));
        return "patient/patient-detail";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editPatientFrom(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("patient", patientService.findById(id));
        model.addAttribute("newPatient", patientService.findById(id).getNumber());
        model.addAttribute("addStatus", false);
        model.addAttribute("title", Title.values());
        model.addAttribute("gender", Gender.values());
        return "patient/addPatient";
    }

    //common attribute collect to one method
    private void commonModel(Model model) {
        String input = patientService.lastPatient().getNumber();
        String patientNumber = input.replaceAll("[^0-9]+", "");
        int PatientNumber = Integer.parseInt(patientNumber);
        int newPatientNumber = PatientNumber + 1;
        model.addAttribute("addStatus", true);
        model.addAttribute("messageArea", false);
        model.addAttribute("lastPatient", input);
        model.addAttribute("newPatient", "EHS" + newPatientNumber);
        model.addAttribute("title", Title.values());
        model.addAttribute("gender", Gender.values());
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String patientAddFrom(Model model) {
        commonModel(model);
        model.addAttribute("patient", new Patient());
        return "patient/addPatient";
    }

    // Above method support to send data to front end - All List, update, edit
    //Bellow method support to do back end function save, delete, update, search

    @RequestMapping(value = {"/add", "/update"}, method = RequestMethod.POST)
    public String addPatient(@Valid @ModelAttribute Patient patient, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = userService.findByUserIdByUserName(auth.getName());
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            commonModel(model);
            return "patient/addPatient";
        }
        //check patient is already in the system or not (Patient update)
        if (patient.getId() != null) {
            patient.setUpDateUser(userService.findById(userId));
            patient.setUpdatedAt(dateTimeAgeService.getCurrentDate());
            patientService.persist(patient);

            if (patient.getEmail() != null) {
                String message = "Welcome to Excellent Health Solution \n " +
                        "Your detail is updated" +
                        "\n\n\n\n\n Please inform us to if there is any changes on your details" +
                        "\n Kindly request keep your data up to date with us. so we can provide better service for you." +
                        "\n \n \n   Thank You" +
                        "\n Excellent Health Solution" +
                        "\n\n\n\n" +
                        "This is a one way communication email service \n please do not reply";
                boolean isFlag = emailService.sendPatientRegistrationEmail(patient.getEmail(), "Welcome to Excellent Health Solution ", message);
                if (isFlag) {
                    redirectAttributes.addFlashAttribute("message", "Successfully Update and Email was sent.");
                    redirectAttributes.addFlashAttribute("alertStatus", true);
                    redirectAttributes.addFlashAttribute("messageArea", true);
                    return "redirect:/patient";
                } else {
                    redirectAttributes.addFlashAttribute("message", "Successfully Update but Email was not sent.");
                    redirectAttributes.addFlashAttribute("alertStatus", false);
                    redirectAttributes.addFlashAttribute("messageArea", true);
                    return "redirect:/patient";
                }
            }
            return "redirect:/patient";
        }
// patient create
        patient.setCreatedUser(userService.findById(userId));
        patient.setCreatedAt(dateTimeAgeService.getCurrentDate());
        patientService.persist(patient);
        if (patient.getEmail() != null) {
            String message = "Welcome to Excellent Health Solution \n " +
                    "Your registration number is " + patient.getNumber() +
                    "\nYour Details are" +
                    "\n " + patient.getTitle().getTitle() + " " + patient.getName() +
                    "\n " + patient.getNic() +
                    "\n " + patient.getDateOfBirth() +
                    "\n " + patient.getMobile() +
                    "\n " + patient.getLand() +
                    "\n\n\n\n\n Please inform us to if there is any changes on your details" +
                    "\n Kindly request keep your data up to date with us. so we can provide better service for you." +
                    "\n \n \n   Thank You" +
                    "\n Excellent Health Solution" +
                    "This is a one way communication email service \n please don reply";
            boolean isFlag = emailService.sendPatientRegistrationEmail(patient.getEmail(), "Welcome to Excellent Health Solution ", message);
            if (isFlag) {
                redirectAttributes.addFlashAttribute("message", "Successfully Add and Email was sent.");
                redirectAttributes.addFlashAttribute("alertStatus", true);
                redirectAttributes.addFlashAttribute("messageArea", true);

                return "redirect:/patient";
            } else {
                redirectAttributes.addFlashAttribute("message", "Successfully Add but Email was not sent.");
                redirectAttributes.addFlashAttribute("alertStatus", false);
                redirectAttributes.addFlashAttribute("messageArea", true);
                return "redirect:/patient";
            }
        }

        return "redirect:/patient";
    }


    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public String removePatient(@PathVariable Integer id) {
        patientService.delete(id);
        return "redirect:/patient";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, Patient patient) {
        model.addAttribute("patientDetail", patientService.search(patient));
        return "patient/patient-detail";
    }

}
