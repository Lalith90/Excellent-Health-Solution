package lk.solution.health.excellent.transaction.service;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lk.solution.health.excellent.common.interfaces.AbstractService;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.resource.entity.Patient;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.transaction.dao.InvoiceDao;
import lk.solution.health.excellent.transaction.entity.Invoice;
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
import java.time.LocalDate;
import java.util.List;

@Service
public  class InvoiceService implements AbstractService<Invoice, Integer> {
    private static Logger logger = LoggerFactory.getLogger(LabTestService.class);
    private InvoiceDao invoiceDao;
    @Autowired
    public InvoiceService(InvoiceDao invoiceDao){
        this.invoiceDao = invoiceDao;
    }

    @Transactional
    public List<Invoice> findAll() {
        return invoiceDao.findAll();
    }

    @Transactional
    public Invoice findById(Integer id) {
        return invoiceDao.getOne(id);
    }

    @Transactional
    public Invoice persist(Invoice invoice) {
        return invoiceDao.save(invoice);
    }

    @Transactional
    public boolean delete(Integer id) {
        invoiceDao.deleteById(id);
        return false;
    }

    @Transactional
    public List<Invoice> search(Invoice invoice) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Invoice> invoiceExample = Example.of(invoice, matcher);
        return invoiceDao.findAll(invoiceExample);
    }
    @Transactional
    public List<Invoice> findByPatient(Patient patient) {
        return invoiceDao.findByPatient(patient);
    }

    @Transactional
    public Invoice findByPatientAndCreateDate(Patient patient, LocalDate date) {
        return invoiceDao.findByPatientAndCreatedAt(patient, date);
    }
    @Transactional
    public List<Invoice> findByDateAndUser(LocalDate currentDate, User byUserName) {
        return invoiceDao.findByCreatedAtAndUser(currentDate, byUserName);
    }
    @Transactional
    public List<Invoice> findByDate(LocalDate today) {
        return invoiceDao.findByCreatedAt(today);
    }
    @Transactional
    public Integer countByCreatedAt(LocalDate today) {
        return invoiceDao.countByCreatedAt(today);
    }
    @Transactional
    public List<Invoice> findByCreatedAtIsBetween(LocalDate from, LocalDate to) {
        return invoiceDao.findByCreatedAtIsBetween(from, to);
    }
    @Transactional
    public Integer countByCreatedAtIsBetween(LocalDate from, LocalDate to) {
        return invoiceDao.countByCreatedAtIsBetween(from,to);
    }

    @Transactional
    public boolean createPdf(Invoice invoice1, ServletContext context, HttpServletRequest request, HttpServletResponse response) {

        System.out.println("invoice service");
        System.out.println(invoice1.getId());
        Document document = new Document(PageSize.A4, 15, 15, 45,30);
        try {
            String filePath = context.getRealPath("/resources/report");
            File file = new File(filePath);
            boolean exists = new File(filePath).exists();
            if (!exists){
                new  File(filePath).mkdirs();
            }
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file+"/"+"employees"+".pdf"));
            document.open();
            Font mainFont = FontFactory.getFont("Arial", 10, BaseColor.BLACK);


            Paragraph paragraph = new Paragraph("Escellent Health Solution", mainFont);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setIndentationLeft(50);
            paragraph.setIndentationRight(50);
            paragraph.setSpacingAfter(10);
            document.add(paragraph);

            PdfPTable doctorRow = new PdfPTable(2);//column amount
            PdfPCell cell1 = new PdfPCell(new Phrase("Name              :   "+invoice1.getPatient().getTitle().getTitle()+" "+ invoice1.getPatient().getName(),mainFont));
            cell1.setBorder(0);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorderColor(BaseColor.WHITE);
            doctorRow.addCell(cell1);
            PdfPCell cell2 = new PdfPCell(new Phrase("Invoice REFF.   :   "+invoice1.getId(), mainFont));
            cell2.setBorder(0);
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.setBorderColor(BaseColor.WHITE);
            doctorRow.addCell(cell2);

            Font tableHeader = FontFactory.getFont("Arial", 10, BaseColor.BLACK);
            Font tableBody = FontFactory.getFont("Arial", 9, BaseColor.BLACK);

            float[] columnWidths = {2f, 2f, 2f, 2f};
            doctorRow.setWidths(columnWidths);

            PdfPTable table = new PdfPTable(4);//column amount
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10);
            table.setWidths(columnWidths);


            for (InvoiceHasLabTest invoiceHasLabTest : invoice1.getInvoiceHasLabTests()){
                PdfPCell nameValue = new PdfPCell(new Paragraph(invoiceHasLabTest.getLabTest().getName(), tableHeader));
                nameValue.setBorderColor(BaseColor.BLACK);
                nameValue.setPaddingLeft(10);
                nameValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                nameValue.setVerticalAlignment(Element.ALIGN_CENTER);
                nameValue.setBackgroundColor(BaseColor.WHITE);
                nameValue.setExtraParagraphSpace(5f);
                table.addCell(nameValue);

                PdfPCell emailValue = new PdfPCell(new Paragraph(String.valueOf(invoiceHasLabTest.getLabTest().getPrice()), tableHeader));
                emailValue.setBorderColor(BaseColor.BLACK);
                emailValue.setPaddingLeft(10);
                emailValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                emailValue.setVerticalAlignment(Element.ALIGN_CENTER);
                emailValue.setBackgroundColor(BaseColor.WHITE);
                emailValue.setExtraParagraphSpace(5f);
                table.addCell(emailValue);

                PdfPCell mobileValue = new PdfPCell(new Paragraph(invoiceHasLabTest.getLabTest().getCode(), tableHeader));
                mobileValue.setBorderColor(BaseColor.BLACK);
                mobileValue.setPaddingLeft(10);
                mobileValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                mobileValue.setVerticalAlignment(Element.ALIGN_CENTER);
                mobileValue.setBackgroundColor(BaseColor.WHITE);
                mobileValue.setExtraParagraphSpace(5f);
                table.addCell(mobileValue);

                PdfPCell addressValue = new PdfPCell(new Paragraph(invoiceHasLabTest.getInvoice().getPatient().getName(), tableHeader));
                addressValue.setBorderColor(BaseColor.BLACK);
                addressValue.setPaddingLeft(10);
                addressValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                addressValue.setVerticalAlignment(Element.ALIGN_CENTER);
                addressValue.setBackgroundColor(BaseColor.WHITE);
                addressValue.setExtraParagraphSpace(5f);
                table.addCell(addressValue);
            }

            document.add(table);
            document.close();
            writer.close();
            return true;


        }catch (Exception e){
            logger.error(e.toString());
            return false;
        }
    }
}
