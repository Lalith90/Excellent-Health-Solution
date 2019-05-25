package lk.solution.health.excellent.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Service
public class FileHandelService {
    private final ServletContext context;
    private static Logger logger = LoggerFactory.getLogger(FileHandelService.class);
    @Autowired
    public FileHandelService(ServletContext context) {
        this.context = context;
    }

    public boolean fileDownload(String fullPath, HttpServletResponse response, String files) {
        File file = new File(fullPath);
        final int BUFFER_SIZE = 4096;
        if (file.exists()){
            try {
                FileInputStream inputStream = new FileInputStream(file);
                String mimeType = context.getMimeType(fullPath);
                response.setContentType(mimeType);
                response.setHeader("content-disposition:inline; ", "filename="+ files);
                OutputStream outputStream = response.getOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytestRead = -1;
                while ((bytestRead = inputStream.read(buffer))!= -1){
                    outputStream.write(buffer, 0, bytestRead);

                }
                inputStream.close();

                outputStream.close();

                file.delete();

            }catch (Exception e){
                e.printStackTrace();
                logger.error(e.toString());
                return false;
            }
        }
        return true;
    }
}
