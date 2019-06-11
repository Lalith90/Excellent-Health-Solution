package lk.solution.health.excellent.lab.service;

import lk.solution.health.excellent.util.interfaces.AbstractService;
import lk.solution.health.excellent.util.service.DateTimeAgeService;
import lk.solution.health.excellent.lab.dao.LabTestDao;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.entity.LabTestParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabTestService implements AbstractService<LabTest, Integer> {

    private final LabTestDao labTestDao;
    private final DateTimeAgeService dateTimeAgeService;
    private static Logger logger = LoggerFactory.getLogger(LabTestService.class);

    @Autowired
    public LabTestService(LabTestDao labTestDao, DateTimeAgeService dateTimeAgeService) {
        this.labTestDao = labTestDao;
        this.dateTimeAgeService = dateTimeAgeService;
    }

    @Cacheable("labTest")
    public List<LabTest> findAll() {
        System.out.println("Lab Test cache ok");
        return labTestDao.findAll();
    }
    @CachePut(value = "labTest")
    public LabTest findById(Integer id) {
        System.out.println("lat test find by id ");
        return labTestDao.getOne(id);
    }

    @CachePut(value = "labTest")
    public LabTest persist(LabTest labTest) {
        return labTestDao.save(labTest);
    }

    @CacheEvict(value = "labTest", allEntries = true)
    public boolean delete(Integer id) {
        labTestDao.deleteById(id);
        return false;
    }
    @CachePut(value = "labTest")
    public List<LabTest> search(LabTest labTest) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<LabTest> laboratoryTestExample = Example.of(labTest, matcher);
        return labTestDao.findAll(laboratoryTestExample);
    }
    @CachePut(value = "labTest")
    public LabTest findByCode(String code) {
        return labTestDao.findByCode(code);
    }


   /*
    public boolean createPdf(InvoiceHasLabTest invoiceHasLabTest, ServletContext context,HttpServletRequest request, HttpServletResponse response) {

      //6.5cm top
      //3.3cm bottom
        Document document = new Document(PageSize.A4, 25, 25, 175,30);

        try {
            String filePath = context.getRealPath("/resources/report");
            File file = new File(filePath);
            boolean exists = new File(filePath).exists();
            if (!exists){
                new  File(filePath).mkdirs();
            }
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file+"/"+invoiceHasLabTest.getInvoice().getPatient().getName() + ".pdf"));
            document.open();
            Font mainFont = FontFactory.getFont("Times New Roman", 10, BaseColor.BLACK);
            Font tableHeader = FontFactory.getFont("Times New Roman", 10,Font.BOLD, BaseColor.BLACK );
            Font tableBody = FontFactory.getFont("Times New Roman", 9, BaseColor.BLACK);

            float[] columnWidths = {2f, 2f, 2f, 2f};

            PdfPTable doctorRow = new PdfPTable(2);//column amount
            doctorRow.setKeepTogether(true);
            PdfPCell cellp1 = new PdfPCell(new Phrase("PATIENT NAME   :   "+invoiceHasLabTest.getInvoice().getPatient().getTitle().getTitle()+" "+invoiceHasLabTest.getInvoice().getPatient().getName(), mainFont));
            cellp1.setBorder(0);
            cellp1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellp1.setBorderColor(BaseColor.WHITE);
            doctorRow.addCell(cellp1);
            PdfPCell cellp2 = new PdfPCell(new Phrase(" ", mainFont));
            cellp2.setBorder(0);
            cellp2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellp2.setBorderColor(BaseColor.WHITE);
            doctorRow.addCell(cellp1);
            PdfPCell celld1 = new PdfPCell(new Phrase("DOCTOR NAME   :   "+invoiceHasLabTest.getInvoice().getDoctor().getTitle().getTitle()+" "+invoiceHasLabTest.getInvoice().getDoctor().getName(), mainFont));
            celld1.setBorder(0);
            celld1.setHorizontalAlignment(Element.ALIGN_LEFT);
            celld1.setBorderColor(BaseColor.WHITE);
            doctorRow.addCell(celld1);
            PdfPCell celld2 = new PdfPCell(new Phrase(" ", mainFont));
            celld2.setBorder(0);
            celld2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celld2.setBorderColor(BaseColor.WHITE);
            doctorRow.addCell(celld2);
            PdfPCell cell1 = new PdfPCell(new Phrase("AGE              :   "+dateTimeAgeService.getAge(invoiceHasLabTest.getInvoice().getPatient().getDateOfBirth()),mainFont));
            cell1.setBorder(0);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorderColor(BaseColor.WHITE);
            doctorRow.addCell(cell1);
            PdfPCell cell2 = new PdfPCell(new Phrase("LAB REFF.   :   "+dateTimeAgeService.getAge(invoiceHasLabTest.getInvoice().getPatient().getDateOfBirth()), mainFont));
            cell2.setBorder(0);
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.setBorderColor(BaseColor.WHITE);
            doctorRow.addCell(cell2);
            PdfPCell cell3 = new PdfPCell(new Phrase("SAMPLE COLLECTED   :  "+invoiceHasLabTest.getSampleCollectedDateTime().toLocalDate()+" "+ invoiceHasLabTest.getSampleCollectedDateTime().toLocalTime().getHour()+":"+invoiceHasLabTest.getSampleCollectedDateTime().toLocalTime().getMinute(), mainFont));
            cell3.setBorder(0);
            cell3.setBorderColor(BaseColor.WHITE);
            cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
            doctorRow.addCell(cell3);
            PdfPCell cell4 = new PdfPCell(new Phrase("PRINTED     :   "+invoiceHasLabTest.getReportPrintedDateTime().toLocalDate()+" "+invoiceHasLabTest.getReportPrintedDateTime().toLocalTime().getHour()+":"+invoiceHasLabTest.getReportPrintedDateTime().toLocalTime().getMinute(), mainFont));
            cell4.setBorder(0);
            cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell4.setBorderColor(BaseColor.WHITE);
            doctorRow.addCell(cell4);


            Paragraph paragraph0 = new Paragraph(invoiceHasLabTest.getLabTest().getName(), mainFont);
            paragraph0.setAlignment(Element.ALIGN_CENTER);
            paragraph0.setIndentationLeft(50);
            paragraph0.setIndentationRight(50);
            paragraph0.setSpacingAfter(10);


            Paragraph paragraphEnd = new Paragraph("-- End Of Report --", mainFont);
            paragraphEnd.setAlignment(Element.ALIGN_CENTER);
            paragraphEnd.setIndentationLeft(50);
            paragraphEnd.setIndentationRight(50);
            paragraphEnd.setSpacingAfter(10);

// create to lab test result display
            PdfPTable table = new PdfPTable(4);//column amount
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10);
            table.setWidths(columnWidths);

            PdfPCell name = new PdfPCell(new Paragraph("TEST NAME", tableHeader));
            name.setBorder(0);
            name.setPaddingLeft(10);
            name.setHorizontalAlignment(Element.ALIGN_CENTER);
            name.setVerticalAlignment(Element.ALIGN_CENTER);
            name.setExtraParagraphSpace(5f);
            table.addCell(name);

            PdfPCell result = new PdfPCell(new Paragraph("RESULT", tableHeader));
            result.setBorder(0);
            result.setPaddingLeft(10);
            result.setHorizontalAlignment(Element.ALIGN_CENTER);
            result.setVerticalAlignment(Element.ALIGN_CENTER);
            result.setExtraParagraphSpace(5f);
            table.addCell(result);

            PdfPCell unit = new PdfPCell(new Paragraph("UNIT", tableHeader));
            unit.setBorder(0);
            unit.setPaddingLeft(10);
            unit.setHorizontalAlignment(Element.ALIGN_CENTER);
            unit.setVerticalAlignment(Element.ALIGN_CENTER);
            unit.setExtraParagraphSpace(5f);
            table.addCell(unit);

            PdfPCell RefRange = new PdfPCell(new Paragraph("REF.RANGE", tableHeader));
            RefRange.setBorder(0);
            RefRange.setPaddingLeft(10);
            RefRange.setHorizontalAlignment(Element.ALIGN_CENTER);
            RefRange.setVerticalAlignment(Element.ALIGN_CENTER);
            RefRange.setExtraParagraphSpace(5f);
            table.addCell(RefRange);

            for (LabTestParameter labTestParameter : invoiceHasLabTest.getLabTest().getLabTestParameters()){
                PdfPCell nameValue = new PdfPCell(new Paragraph(labTestParameter.getName(), tableBody));
                nameValue.setBorder(0);
                nameValue.setPaddingLeft(10);
                nameValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                nameValue.setVerticalAlignment(Element.ALIGN_LEFT);
                nameValue.setBackgroundColor(BaseColor.WHITE);
                nameValue.setExtraParagraphSpace(5f);
                table.addCell(nameValue);

                PdfPCell resultValue = new PdfPCell(new Paragraph(labTestParameter.getResult(), tableBody));
                resultValue.setBorder(0);
                resultValue.setPaddingLeft(10);
                resultValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                resultValue.setVerticalAlignment(Element.ALIGN_LEFT);
                resultValue.setBackgroundColor(BaseColor.WHITE);
                resultValue.setExtraParagraphSpace(5f);
                table.addCell(resultValue);
                
                PdfPCell unitValue = new PdfPCell(new Paragraph(labTestParameter.getUnit(), tableBody));
                unitValue.setBorder(0);
                unitValue.setPaddingLeft(10);
                unitValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                unitValue.setVerticalAlignment(Element.ALIGN_LEFT);
                unitValue.setBackgroundColor(BaseColor.WHITE);
                unitValue.setExtraParagraphSpace(5f);
                table.addCell(unitValue);
                
                PdfPCell rangeValue = new PdfPCell(new Paragraph(labTestParameter.getMin() +" - "+labTestParameter.getMax(), tableBody));
                rangeValue.setBorder(0);
                rangeValue.setPaddingLeft(10);
                rangeValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                rangeValue.setVerticalAlignment(Element.ALIGN_LEFT);
                rangeValue.setBackgroundColor(BaseColor.WHITE);
                rangeValue.setExtraParagraphSpace(5f);
                table.addCell(rangeValue);
            }



            document.add(doctorRow);
            document.add(paragraph0);
            document.add(table);
            document.add(paragraphEnd);


            document.close();
            writer.close();
            return true;


        }catch (Exception e){
            logger.error(e.toString());
            return false;
        }
    }*/


    public List<LabTest> findByLabTestParameter(LabTestParameter s) {
        return labTestDao.findByLabTestParameters(s);
    }
}
