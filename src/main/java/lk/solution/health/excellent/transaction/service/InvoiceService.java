package lk.solution.health.excellent.transaction.service;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lk.solution.health.excellent.common.interfaces.AbstractService;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.resource.entity.CollectingCenter;
import lk.solution.health.excellent.resource.entity.Patient;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.resource.service.CollectingCenterService;
import lk.solution.health.excellent.transaction.dao.InvoiceDao;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class InvoiceService implements AbstractService<Invoice, Integer> {
    private static Logger logger = LoggerFactory.getLogger(LabTestService.class);

    private InvoiceDao invoiceDao;
    private CollectingCenterService collectingCenterService;
    private InvoiceHasLabTestService invoiceHasLabTestService;

    @Autowired
    public InvoiceService(InvoiceDao invoiceDao, CollectingCenterService collectingCenterService, InvoiceHasLabTestService invoiceHasLabTestService) {
        this.invoiceDao = invoiceDao;
        this.collectingCenterService = collectingCenterService;
        this.invoiceHasLabTestService = invoiceHasLabTestService;
    }


    public List<Invoice> findAll() {
        return invoiceDao.findAll();
    }


    public Invoice findById(Integer id) {
        return invoiceDao.getOne(id);
    }

    @Transactional
    public Invoice persist(Invoice invoice) {
        return invoiceDao.save(invoice);
    }


    public boolean delete(Integer id) {
        invoiceDao.deleteById(id);
        return false;
    }


    public List<Invoice> search(Invoice invoice) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Invoice> invoiceExample = Example.of(invoice, matcher);
        return invoiceDao.findAll(invoiceExample);
    }

    public Invoice findLastInvoice() {
        return invoiceDao.findFirstByOrderByIdDesc();
    }

    public List<Invoice> findByPatient(Patient patient) {
        return invoiceDao.findByPatient(patient);
    }

    public Invoice findByPatientAndCreateDate(Patient patient, LocalDateTime date) {
        return invoiceDao.findByPatientAndCreatedAt(patient, date);
    }

    public List<Invoice> findByDateAndUser(LocalDateTime currentDate, User byUserName) {
        return invoiceDao.findByCreatedAtAndUser(currentDate, byUserName);
    }

    public List<Invoice> findByDate(LocalDateTime today) {
        return invoiceDao.findByCreatedAt(today);
    }

    public Integer countByCreatedAt(LocalDateTime today) {
        return invoiceDao.countByCreatedAt(today);
    }

    public List<Invoice> findByCreatedAtIsBetween(LocalDateTime from, LocalDateTime to) {
        return invoiceDao.findByCreatedAtIsBetween(from, to);
    }

    public Integer countByCreatedAtIsBetween(LocalDateTime from, LocalDateTime to) {
        return invoiceDao.countByCreatedAtIsBetween(from, to);
    }

    // common style for several phParagraph
    private void commonStyleForParagraph(Paragraph paragraph) {
        paragraph.setAlignment(Element.ALIGN_CENTER);
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

    private PdfPTable labTestToTable(List<LabTest> labTests, boolean medicalPackage) {
        System.out.println("come to lab test table");
        //Font
        Font secondaryFont = FontFactory.getFont("Didot", 9, BaseColor.BLACK);
        //create a table
        float[] columnWidth = {10f, 240f, 150f};//column amount{column 1 , column 2 , column 3}
        PdfPTable labTestTable = new PdfPTable(columnWidth);

        int labTestCount = 1;
        for (LabTest labTest : labTests) {
            //cell 1
            PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(labTestCount), secondaryFont));
            commonStyleForPdfPCell(cell);
            labTestTable.addCell(cell);
            //cell 2
            PdfPCell cell1 = new PdfPCell(new Phrase(labTest.getName(), secondaryFont));
            commonStyleForPdfPCell(cell1);
            labTestTable.addCell(cell1);
            //cell 3
            String medicalPackageOrNotValue = "";
            if (!medicalPackage) {
                System.out.println("come to only lab test");
                medicalPackageOrNotValue = labTest.getPrice().setScale(2, BigDecimal.ROUND_CEILING).toString();
            }
            PdfPCell cell2 = new PdfPCell(new Phrase(medicalPackageOrNotValue, secondaryFont));
            commonStyleForPdfPCellLastOne(cell2);
            labTestTable.addCell(cell2);

            labTestCount++;
        }

        return labTestTable;
    }

    public boolean createPdf(Invoice invoice, ServletContext context, HttpServletRequest request, HttpServletResponse response) {

        CollectingCenter collectingCenter = collectingCenterService.firstCollectingCenter();

        try {
            String filePath = context.getRealPath("/resources/report");
            File file = new File(filePath);
            boolean exists = new File(filePath).exists();
            if (!exists) {
                new File(filePath).mkdirs();
            }

            Document document = new Document(PageSize.LETTER, 15, 15, 15, 0);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file + "/" + invoice.getPatient().getTitle().getTitle() + " " + invoice.getPatient().getName() + "invoice" + ".pdf"));

            document.open();
            //All front
            Font mainFont = FontFactory.getFont("Arial", 12, BaseColor.BLACK);
            Font secondaryFont = FontFactory.getFont("Arial", 9, BaseColor.BLACK);


            Paragraph paragraph = new Paragraph("Excellent Health Solution", mainFont);
            commonStyleForParagraph(paragraph);
            document.add(paragraph);

            Paragraph paragraph1 = new Paragraph("-------------------------------------------------------------------------------", secondaryFont);
            commonStyleForParagraph(paragraph1);
            document.add(paragraph1);

            Paragraph paragraph2 = new Paragraph(collectingCenter.getAddress(), secondaryFont);
            commonStyleForParagraph(paragraph2);
            document.add(paragraph2);

            Paragraph paragraph3 = new Paragraph("Mobile : " + collectingCenter.getMobile() + " Land : " + collectingCenter.getLand(), secondaryFont);
            commonStyleForParagraph(paragraph3);
            document.add(paragraph3);

            Paragraph paragraph4 = new Paragraph("Email : " + collectingCenter.getEmail(), secondaryFont);
            commonStyleForParagraph(paragraph4);
            document.add(paragraph4);

            Paragraph paragraph5 = new Paragraph("--------------------------------------------------------------------------------------------------------------------\n", mainFont);
            commonStyleForParagraph(paragraph5);
            document.add(paragraph5);

            //Create a Table (Patient Details)
            float[] columnWidths = {200f, 200f};//column amount{column 1 , column 2 }
            PdfPTable mainTable = new PdfPTable(columnWidths);
            // add cell to table
            PdfPCell cell = new PdfPCell(new Phrase("Date : \t" + invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss")), secondaryFont));
            commonStyleForPdfPCell(cell);
            mainTable.addCell(cell);

            PdfPCell cell1 = new PdfPCell(new Phrase("Invoice Number : " + invoice.getNumber(), secondaryFont));
            commonStyleForPdfPCell(cell1);
            mainTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Name : " + invoice.getPatient().getTitle().getTitle() + " " + invoice.getPatient().getName(), secondaryFont));
            commonStyleForPdfPCell(cell2);
            mainTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Tel : " + invoice.getPatient().getMobile() + " / " + invoice.getPatient().getLand(), secondaryFont));
            commonStyleForPdfPCell(cell3);
            mainTable.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Referral Doctor : " + invoice.getDoctor().getTitle().getTitle() + " " + invoice.getDoctor().getName(), secondaryFont));
            commonStyleForPdfPCell(cell4);
            mainTable.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Phrase(invoice.getDoctor().getConsultation().getName(), secondaryFont));
            commonStyleForPdfPCell(cell5);
            mainTable.addCell(cell5);

            document.add(mainTable);

            //Lab Test and Medical Package details

            if (invoice.getMedicalPackage() == null && invoice.getInvoiceHasLabTests() != null) {
                List<LabTest> labTests = new ArrayList<>();
                List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByInvoice(invoice);
                for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests) {
                    labTests.add(invoiceHasLabTest.getLabTest());
                }
                document.add(labTestToTable(labTests, false));
            }
            if (invoice.getMedicalPackage() != null && invoice.getInvoiceHasLabTests() == null) {
                List<LabTest> labTests = invoice.getMedicalPackage().getLabTests();
                document.add(labTestToTable(labTests, true));
            }
            if (invoice.getMedicalPackage() != null && invoice.getInvoiceHasLabTests() != null) {
                System.out.println("Both are inclueded");
                List<LabTest> labTests = new ArrayList<>(invoiceHasLabTestService.findLabTestByInvoice(invoice));
                System.out.println("not null");
                document.add(labTestToTable(labTests, false));

                Paragraph paragraph6 = new Paragraph("Medical Package details", secondaryFont);
                commonStyleForParagraph(paragraph6);
                document.add(paragraph6);

                List<LabTest> labTests1 = invoice.getMedicalPackage().getLabTests();
                document.add(labTestToTable(labTests1, true));
            }


            document.close();
            writer.close();
            return true;


        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
    }
}
/*
    // Creating a PdfDocument object
    String dest = "C:/itextExamples/addingTable.pdf";
                        PdfWriter writer = new PdfWriter(dest);

    // Creating a PdfDocument object
                        PdfDocument pdf = new PdfDocument(writer);

    // Creating a Document object
                        Document doc = new Document(pdf);

    // Creating a table
                        float [] pointColumnWidths = {150F, 150F, 150F};
                        Table table = new Table(pointColumnWidths);

// Adding cells to the table
                      table.addCell(new Cell().add("Name"));
                      table.addCell(new Cell().add("Raju"));
                      table.addCell(new Cell().add("Id"));
                      table.addCell(new Cell().add("1001"));
                      table.addCell(new Cell().add("Designation"));
                      table.addCell(new Cell().add("Programmer"));

// Adding Table to document
                    doc.add(table);
// Closing the document
                    doc.close();*/
