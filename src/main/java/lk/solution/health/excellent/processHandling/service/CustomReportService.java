package lk.solution.health.excellent.processHandling.service;

import org.springframework.stereotype.Service;

@Service
public class CustomReportService {
    //excel file

/*    public boolean createExcell(List<Employee> employees, ServletContext context, HttpServletRequest request, HttpServletResponse response) {

        String filePath = context.getRealPath("/resources/report");
        File file = new File(filePath);
        boolean exists = new File(filePath).exists();
        if (!exists){
            new File(filePath).mkdirs();
        }
        try{
            FileOutputStream outputStream = new FileOutputStream(file+"/"+"employees"+".xls");
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet workSheet = workbook.createSheet("employees");
            workSheet.setDefaultColumnWidth(30);

            HSSFCellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
            headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            HSSFRow headerRow = workSheet.createRow(0);

            HSSFCell name = headerRow.createCell(0);
            name.setCellStyle(headerCellStyle);

            HSSFCell email = headerRow.createCell(1);
            email.setCellStyle(headerCellStyle);

            HSSFCell mobile = headerRow.createCell(2);
            mobile.setCellStyle(headerCellStyle);

            HSSFCell address = headerRow.createCell(3);
            address.setCellStyle(headerCellStyle);

            Integer i = 1;

            for (Employee employee : employees){
                HSSFRow bodyRow = workSheet.createRow(i);

                HSSFCellStyle bodyCellStyle = workbook.createCellStyle();
                bodyCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);

                HSSFCell nameValue = bodyRow.createCell(0);
                nameValue.setCellValue(employee.getName());
                nameValue.setCellStyle(bodyCellStyle);

                HSSFCell emailValue = bodyRow.createCell(1);
                emailValue.setCellValue(employee.getEmail());
                emailValue.setCellStyle(bodyCellStyle);

                HSSFCell mobileValue = bodyRow.createCell(2);
                mobileValue.setCellValue(employee.getMobile());
                mobileValue.setCellStyle(bodyCellStyle);

                HSSFCell addressValue = bodyRow.createCell(3);
                addressValue.setCellValue(employee.getAddress());
                addressValue.setCellStyle(bodyCellStyle);

                i++;
            }

            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            return true;

        }catch (Exception e){
            return false;
        }
    }*/
}
