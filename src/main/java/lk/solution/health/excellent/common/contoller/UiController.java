package lk.solution.health.excellent.common.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    @GetMapping("/index")
    public String showIndexPage() {
        return "index";
    }

    @GetMapping("/home")
    public String mainwindow(){
         return "mainwindow";
    }

    @GetMapping("/invoice1")
    public String invoice(){
        return "invoice/invoice1";
    }

    @GetMapping("/fonts/glyphicons-halflings-regular.ttf")
    public String login(){
        return "mainwindow";
    }

/*    @GetMapping("/loginErr")
    public String loginErr(Model model){
        model.addAttribute("error", true);
        return "login/login";
    }*/

    }
