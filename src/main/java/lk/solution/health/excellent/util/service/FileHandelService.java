package lk.solution.health.excellent.util.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Service
public class FileHandelService {
    private static Logger logger = LoggerFactory.getLogger(FileHandelService.class);
    private final ServletContext context;

    @Autowired
    public FileHandelService(ServletContext context) {
        this.context = context;
    }

    public ModelAndView fileDownload(String fullPath, HttpServletResponse response, String files) {
        File file = new File(fullPath);
        final int BUFFER_SIZE = 4096;
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                String mimeType = context.getMimeType(fullPath);
               // response.sendRedirect("/home");
                response.setContentType(mimeType);

                response.setHeader("content-disposition:inline; ", "filename=" + files);

                OutputStream outputStream = response.getOutputStream();

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
               // response.flushBuffer();
               file.delete();
            } catch (Exception e) {
                logger.error("file handler "+e.toString());
               // return false;
            }
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");
        modelAndView.addObject("fileName",files);
        return modelAndView;

    }

  /*  public ResponseEntity<InputStreamResource> report(String fullPath, HttpServletResponse response, String files) {
        File file = new File(fullPath);

        ByteArrayInputStream bis = GeneratePdfReport.citiesReport(cities);
        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline;","filename=" + files);


        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }*/

/*
    public void citiesReport(Invoice invoice) {

        ByteArrayInputStream bis =invoiceService.createPdf(invoice, context);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename="+"/resources/report/" + invoice.getPatient().getTitle().getTitle() + "." + invoice.getPatient().getName() + "-invoice" + ".pdf");
        ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
*/


}
