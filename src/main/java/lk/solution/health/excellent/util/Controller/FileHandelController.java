package lk.solution.health.excellent.util.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Controller
public class FileHandelController {
    private static Logger logger = LoggerFactory.getLogger(FileHandelController.class);
    private final ServletContext context;

    @Autowired
    public FileHandelController(ServletContext context) {
        this.context = context;
    }

    @RequestMapping(value = "/getFile/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    public void fileDownload(@PathVariable("id") int id, HttpServletResponse response, HttpServletRequest request) {


        // todo -> this is the place which I was strutted

        String fullPath = request.getServletContext().getRealPath("/resources/report/" + id + ".pdf");
        File file = new File(fullPath);
        final int BUFFER_SIZE = 4096;

        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);

                String mimeType = context.getMimeType(fullPath);

                response.setContentType(mimeType);

                response.setHeader("content-disposition: inline;", "filename=" + id + ".pdf");

                OutputStream outputStream = response.getOutputStream();

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
                response.setStatus(200);
                //response.sendRedirect("/home");
                file.delete();

            } catch (Exception e) {
                logger.error("file handler ++" + e.toString());
            }
        }


    }

}
