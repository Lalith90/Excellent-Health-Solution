package lk.solution.health.excellent.util.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ImageHandlerController {

    @GetMapping("/imageDisplay")
    public void showImage(@RequestParam("id") int imageId, HttpServletResponse response)
            throws ServletException, IOException {


       // Item item = itemService.get(itemId); --> contained class object
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
       // response.getOutputStream().write(item.getItemImage()); need to get image where store it


        response.getOutputStream().close();
    }
}
