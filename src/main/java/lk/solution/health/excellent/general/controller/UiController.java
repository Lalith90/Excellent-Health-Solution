package lk.solution.health.excellent.general.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {
    @GetMapping(value = {"/","/home"})
    public String home() {
        return "mainwindow";
    }

    @GetMapping("/index")
    public String index() {
        return "index1";
    }
}
