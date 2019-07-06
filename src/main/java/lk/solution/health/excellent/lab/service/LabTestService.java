package lk.solution.health.excellent.lab.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.lab.dao.LabTestDao;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.lab.entity.Enum.ParameterHeader;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.entity.LabTestParameter;
import lk.solution.health.excellent.lab.entity.ResultTable;
import lk.solution.health.excellent.util.interfaces.AbstractService;
import lk.solution.health.excellent.util.service.DateTimeAgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LabTestService implements AbstractService<LabTest, Integer> {

    private static Logger logger = LoggerFactory.getLogger(LabTestService.class);
    private final LabTestDao labTestDao;
    private final DateTimeAgeService dateTimeAgeService;
    private final ResultTableService resultTableService;

    @Autowired
    public LabTestService(LabTestDao labTestDao, DateTimeAgeService dateTimeAgeService, ResultTableService resultTableService) {
        this.labTestDao = labTestDao;
        this.dateTimeAgeService = dateTimeAgeService;
        this.resultTableService = resultTableService;
    }

    @Cacheable("labTest")
    public List<LabTest> findAll() {
        return labTestDao.findAll();
    }

    @CachePut(value = "labTest")
    public LabTest findById(Integer id) {
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

    /* Pdf processing start */

    // common style for several phParagraph
    private void commonStyleForParagraph(Paragraph paragraph) {
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setIndentationLeft(50);
        paragraph.setIndentationRight(50);
    }

    private void commonStyleForParagraphTwo(Paragraph paragraph) {
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.setIndentationLeft(50);
        paragraph.setIndentationRight(50);
    }

    // common style for several table cell
    private void commonStyleForPdfPCell(PdfPCell pdfPCell) {
        pdfPCell.setBorder(0);
        pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfPCell.setBorderColor(BaseColor.WHITE);
    }

    // common style for several table cell
    private void commonStyleForPdfPCellLastOne(PdfPCell pdfPCell) {
        pdfPCell.setBorder(0);
        pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pdfPCell.setBorderColor(BaseColor.WHITE);
    }


    public boolean createPdf(InvoiceHasLabTest invoiceHasLabTest, ServletContext context) {
//equation = inch * 72point
        //1 inch == 2.54cm
//A4 is (595px {} -> width, 842px -> height);
        //6.5cm top ==
        //3.3cm bottom
        // left/right margin 1.5cm
        // { 1-> 6cm , 2-> 3cm, 3-> 2cm, 4-> , 5-> }
        Document document = new Document(PageSize.A4, 25, 25, 184, 94);

        try {
            String filePath = context.getRealPath("/resources/report");
            File file = new File(filePath);
            boolean exists = new File(filePath).exists();
            if (!exists) {
                new File(filePath).mkdirs();
            }

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file + "/" + invoiceHasLabTest.getNumber() + ".pdf"));
            document.open();

            // write javascript to
            writer.addJavaScript("this.print({bUI: false, bSilent: true, bShrinkToFit: true}); \r this.closeDoc();",false);

            //All front
            Font mainFont = FontFactory.getFont("Arial", 12, BaseColor.BLACK);
            Font secondaryFont = FontFactory.getFont("Arial", 9, BaseColor.BLACK);
            Font highLiltedFont = FontFactory.getFont(FontFactory.TIMES_BOLD);
//Patient Name
            Paragraph patientName = new Paragraph("Patient Name : " + invoiceHasLabTest.getInvoice().getPatient().getTitle()
                    + invoiceHasLabTest.getInvoice().getPatient().getName());
            commonStyleForParagraph(patientName);
            document.add(patientName);
// Ref doctor
            Paragraph doctorDetails = new Paragraph("Ref. Doctor : " + invoiceHasLabTest.getInvoice().getDoctor().getTitle()
                    + invoiceHasLabTest.getInvoice().getDoctor().getName());
            commonStyleForParagraph(doctorDetails);
            document.add(doctorDetails);
//Medical Center

//Invoice number
            Paragraph invoiceDetails = new Paragraph("Invoiced : " +
                    invoiceHasLabTest.getInvoice().getNumber() + "\t" + invoiceHasLabTest.getInvoice().getInvoicedAt());
            commonStyleForParagraph(invoiceDetails);
            document.add(invoiceDetails);

//Create a Table (Patient Details - start)
            float[] columnWidths = {200f, 200f};//column amount{column 1 , column 2 }
            PdfPTable mainTable = new PdfPTable(columnWidths);
            // add cell to table
            PdfPCell cell = new PdfPCell(new Phrase("Age : \t" + dateTimeAgeService.getAge(invoiceHasLabTest.getInvoice().getPatient().getDateOfBirth()), mainFont));
            commonStyleForPdfPCell(cell);
            mainTable.addCell(cell);

            PdfPCell cell1 = new PdfPCell(new Phrase("Lab Ref Number : " + invoiceHasLabTest.getNumber(), mainFont));
            commonStyleForPdfPCell(cell1);
            mainTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Sample Collected At: " + invoiceHasLabTest.getSampleCollectedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), mainFont));
            commonStyleForPdfPCell(cell2);
            mainTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Printed At: " + invoiceHasLabTest.getReportPrintedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), mainFont));
            commonStyleForPdfPCell(cell3);
            mainTable.addCell(cell3);

            document.add(mainTable);
//Create a Table (Patient Details - end)

//Lab Test Name add start
            Paragraph labTestName = new Paragraph("\n" + invoiceHasLabTest.getLabTest().getName(), mainFont);
            commonStyleForParagraph(labTestName);
            document.add(labTestName);
//Lab Test Name add end

// To print with result or without result add ro document
            document.add(reportBody(invoiceHasLabTest));

// if there is any lab test comment to add document
            if (invoiceHasLabTest.getLabTest().getComment() != null) {
                Paragraph labTestComment = new Paragraph(invoiceHasLabTest.getLabTest().getComment());
                commonStyleForParagraphTwo(labTestComment);
                document.add(labTestComment);
            }
//if there is any special comment in lab test by MLT
            if (invoiceHasLabTest.getComment() != null) {
                Paragraph mLTComment = new Paragraph(invoiceHasLabTest.getComment());
                commonStyleForParagraphTwo(mLTComment);
                document.add(mLTComment);
            }
//sample collecting center details - start

            Paragraph collectingCenterDetails = new Paragraph(
                    invoiceHasLabTest.getInvoice().getCollectingCenter().getAddress()
                            + "\n" + "M : " + invoiceHasLabTest.getInvoice().getCollectingCenter().getMobile() + " L : " + invoiceHasLabTest.getInvoice().getCollectingCenter().getLand()
                            + "\n" + "E : " + invoiceHasLabTest.getInvoice().getCollectingCenter().getEmail(), secondaryFont);
            commonStyleForParagraphTwo(collectingCenterDetails);
            document.add(collectingCenterDetails);

//sample collecting center details - start


            document.close();
            writer.close();
            return true;


        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
    }

    private PdfPTable reportBody(InvoiceHasLabTest invoiceHasLabTest) {

        //Font
        Font tableHeader = FontFactory.getFont("Times New Roman", 10, Font.BOLD, BaseColor.BLACK);
        Font tableBody = FontFactory.getFont("Times New Roman", 9, BaseColor.BLACK);

        //equation = inch * 72point
        //1 inch == 2.54cm
//A4 is (595px {} -> width, 842px -> height);

        //create a table
        float[] columnWidth = {20f, 240f, 150f, 100f, 100f};//column amount{column 1 , column 2 , column 3}
        //todo recalculate with actual report

        PdfPTable parameterWithResultOrNot = new PdfPTable(columnWidth);


//cell 1 = Test Name
        PdfPCell test_name = new PdfPCell(new Paragraph("TEST NAME", tableHeader));
        commonStyleForResult(test_name);
        parameterWithResultOrNot.addCell(test_name);
//cell 2 = Result
        PdfPCell result = new PdfPCell(new Paragraph("RESULT", tableHeader));
        commonStyleForResult(result);
        parameterWithResultOrNot.addCell(result);
//cell 3 = Units
        PdfPCell unit = new PdfPCell(new Paragraph("UNIT", tableHeader));
        commonStyleForResult(unit);
        parameterWithResultOrNot.addCell(unit);
/*cell 4 = if="${invoiceHasLabTest.getLabTest().getCode() == 'HM21' || invoiceHasLabTest.getLabTest().getCode() == 'HM45'}"
          AB. Count
          */
        if (invoiceHasLabTest.getLabTest().getCode() == "HM21" || invoiceHasLabTest.getLabTest().getCode() == "HM45") {
            PdfPCell abCount = new PdfPCell(new Paragraph("Ab. COUNT", tableHeader));
            commonStyleForResult(abCount);
            parameterWithResultOrNot.addCell(abCount);
        }

//cell 5 = Ref. Range
        PdfPCell refRange = new PdfPCell(new Paragraph("REF.RANGE", tableHeader));
        commonStyleForResult(refRange);
        parameterWithResultOrNot.addCell(refRange);
//table header is end

        boolean toPrint = invoiceHasLabTest.getLabTestStatus().equals(LabTestStatus.AUTHORIZED);
        List<ResultTable> parameterWithResult;
        List<LabTestParameter> parameterWithOutResult;
        if (toPrint) {
            //if need to print result report
            parameterWithResult = resultTableService.findByInvoiceHasLabTest(invoiceHasLabTest);

            //parameter with result body - start
            for (ResultTable resultTable : parameterWithResult) {
                //1 parameter name
                PdfPCell parameterName = new PdfPCell(new Phrase(resultTable.getLabTestParameter().getName(), tableBody));
                commonStyleForResult(parameterName);
                parameterWithResultOrNot.addCell(parameterName);
                //if parameter is header there is no need to print other filed
                if (resultTable.getLabTestParameter().getParameterHeader().equals(ParameterHeader.Yes)) {
                    continue;
                }
                //2 result
                PdfPCell resultInTable = new PdfPCell(new Phrase(resultTable.getResult(), tableBody));
                commonStyleForResult(resultInTable);
                parameterWithResultOrNot.addCell(resultInTable);

                //3 unit
                PdfPCell unitInReferentToParameter = new PdfPCell(new Phrase(resultTable.getLabTestParameter().getUnit(), tableBody));
                commonStyleForResult(unitInReferentToParameter);
                parameterWithResultOrNot.addCell(unitInReferentToParameter);
    /*cell 4 = if="${invoiceHasLabTest.getLabTest().getCode() == 'HM21' || invoiceHasLabTest.getLabTest().getCode() == 'HM45'}"
          AB. Count
          */
                if (invoiceHasLabTest.getLabTest().getCode() == "HM21" || invoiceHasLabTest.getLabTest().getCode() == "HM45") {
                    PdfPCell absoluteCount = new PdfPCell(new Phrase(resultTable.getAbsoluteCount(), tableBody));
                    commonStyleForResult(absoluteCount);
                    parameterWithResultOrNot.addCell(absoluteCount);
                }
                //5 Ref Range
                PdfPCell referenceRange = new PdfPCell(new Phrase(resultTable.getLabTestParameter().getMin() + " - " + resultTable.getLabTestParameter().getMax(), tableBody));
                commonStyleForResult(referenceRange);
                parameterWithResultOrNot.addCell(referenceRange);
            }
            //parameter with result body - end

        } else {
            //if need to get work sheet
            parameterWithOutResult = invoiceHasLabTest.getLabTest().getLabTestParameters();

            //parameter with out result body - start
            for (LabTestParameter labTestParameter : parameterWithOutResult) {
                //1 parameter name
                PdfPCell parameterName = new PdfPCell(new Phrase(labTestParameter.getName(), tableBody));
                commonStyleForResult(parameterName);
                parameterWithResultOrNot.addCell(parameterName);
                //if parameter is header there is no need to print other filed
                if (labTestParameter.getParameterHeader().equals(ParameterHeader.Yes)) {
                    continue;
                }
                //2 result
                PdfPCell resultInTable = new PdfPCell(new Phrase("\t\t\t\t\t", tableBody));
                commonStyleForResult(resultInTable);
                parameterWithResultOrNot.addCell(resultInTable);

                //3 unit
                PdfPCell unitInReferentToParameter = new PdfPCell(new Phrase(labTestParameter.getUnit(), tableBody));
                commonStyleForResult(unitInReferentToParameter);
                parameterWithResultOrNot.addCell(unitInReferentToParameter);

                //4 there is no mentioned absolute count but space are allocated
                PdfPCell absoluteCount = new PdfPCell(new Phrase("\t\t", tableBody));
                commonStyleForResult(absoluteCount);
                parameterWithResultOrNot.addCell(absoluteCount);

                //5 Ref Range
                PdfPCell referenceRange = new PdfPCell(new Phrase(labTestParameter.getMin() + " - " + labTestParameter.getMax(), tableBody));
                commonStyleForResult(referenceRange);
                parameterWithResultOrNot.addCell(referenceRange);
            }


        }

        //table body need to create out side method and add to this area


        return parameterWithResultOrNot;
    }

    // common style for commonStyleForResult
    private void commonStyleForResult(PdfPCell pdfPCell) {
        pdfPCell.setBorder(0);
        pdfPCell.setPaddingLeft(10);
        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
        pdfPCell.setExtraParagraphSpace(5f);
    }

    // pdf creation end
    public List<LabTest> findByLabTestParameter(LabTestParameter s) {
        return labTestDao.findByLabTestParameters(s);
    }
}
