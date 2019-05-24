package lk.solution.health.excellent.common.contoller;

        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    @GetMapping({"/index", "/logout"})
    public String showIndexPage() {
        return "index";
    }


    @GetMapping({"/home", "/fonts/glyphicons-halflings-regular.ttf"})
    public String mainwindow() {
        return "mainwindow";
    }


}
