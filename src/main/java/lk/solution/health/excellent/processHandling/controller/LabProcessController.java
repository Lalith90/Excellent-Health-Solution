package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.lab.entity.Enum.LabtestDoneHere;
import lk.solution.health.excellent.lab.entity.Enum.ParameterHeader;
import lk.solution.health.excellent.lab.entity.LabTestParameter;
import lk.solution.health.excellent.lab.entity.ResultTable;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.lab.service.ResultTableService;
import lk.solution.health.excellent.processHandling.helpingClass.SearchProcess;
import lk.solution.health.excellent.resource.entity.Patient;
import lk.solution.health.excellent.resource.service.PatientService;
import lk.solution.health.excellent.resource.service.UserService;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import lk.solution.health.excellent.util.service.DateTimeAgeService;
import lk.solution.health.excellent.util.service.EmailService;
import lk.solution.health.excellent.util.service.UrlBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LabProcessController {

    private final InvoiceHasLabTestService invoiceHasLabTestService;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final DateTimeAgeService dateTimeAgeService;
    private final PatientService patientService;
    private final ResultTableService resultTableService;
    private final ServletContext context;
    private final EmailService emailService;
    private final LabTestService labTestService;
    private final UrlBuilderService urlBuilderService;

    @Autowired
    public LabProcessController(InvoiceHasLabTestService invoiceHasLabTestService, UserService userService, InvoiceService invoiceService, DateTimeAgeService dateTimeAgeService, PatientService patientService, ResultTableService resultTableService, ServletContext context, EmailService emailService, LabTestService labTestService, UrlBuilderService urlBuilderService) {
        this.invoiceHasLabTestService = invoiceHasLabTestService;
        this.userService = userService;
        this.invoiceService = invoiceService;
        this.dateTimeAgeService = dateTimeAgeService;
        this.patientService = patientService;
        this.resultTableService = resultTableService;
        this.context = context;
        this.emailService = emailService;
        this.labTestService = labTestService;
        this.urlBuilderService = urlBuilderService;
    }


    //Sample Collected Investigation List for taken work sheet
    @RequestMapping(value = "/lab/sampleCollect", method = RequestMethod.GET)
    public String sampleCollectedPatient(Model model) {
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.
                findByLabTestState(LabTestStatus.SAMPLECOLLECT)
                .stream()
                .filter((x) -> x.getLabTest().getLabtestDoneHere().equals(LabtestDoneHere.YES))
                .collect(Collectors.toList());

        model.addAttribute("invoiceHasLabTest", invoiceHasLabTests);
        model.addAttribute("addLabSampleCollect", true);
        model.addAttribute("buttonStatus", true);
        model.addAttribute("inputStatus", true);

        return "/labTest/labTestList";
    }

    //save Worksheet printed lab test
    @RequestMapping(value = "/lab/saveWorkSheetPatient", method = RequestMethod.POST)
    public String saveWorksheetTakenPatient(@ModelAttribute SearchProcess searchProcess, HttpServletRequest request, RedirectAttributes redirectAttributes, UriComponentsBuilder uriComponentsBuilder) {
        List<InvoiceHasLabTest> invoiceHasLabTests = searchProcess.getInvoiceHasLabTests();

        //all work sheet taken test add to list
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests) {
            invoiceHasLabTest.setLabTestStatus(LabTestStatus.WORKSHEET);
            invoiceHasLabTest.setWorkSheetTakenUser(userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()));
            invoiceHasLabTest.setWorkSheetTakenDateTime(dateTimeAgeService.getCurrentDateTime());
        }
        //save all lab test list as bulk
        invoiceHasLabTestService.persistBulk(invoiceHasLabTests);

//this for multiple worksheet print
        // need to create temp variable to store array length and
        int arrayLength = 1;
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests) {
            boolean isFlag = labTestService.createPdf(invoiceHasLabTest, context);
            if (isFlag) {

                String fullPath = request.getServletContext().getRealPath("/resources/report/" + invoiceHasLabTest.getNumber() + ".pdf");
                System.out.println("full path " + fullPath);

                String path = "getFile/" + invoiceHasLabTest.getNumber().toString();
                redirectAttributes.addFlashAttribute("fileName", urlBuilderService.doSomething(uriComponentsBuilder, path));

                if (arrayLength == invoiceHasLabTests.size()) {
                    redirectAttributes.addFlashAttribute("redirectPath", urlBuilderService.doSomething(uriComponentsBuilder, "lab/sampleCollect"));
                }
                arrayLength++;
                return "redirect:/lab/resultPrint";

            }
        }
        return "redirect:/lab/sampleCollect";
    }

    //worksheet taken list for result enter
    @RequestMapping(value = "/lab/worksheetPrinted", method = RequestMethod.GET)
    public String sampleWorksheetPatient(Model model) {
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByLabTestState(LabTestStatus.WORKSHEET);
        model.addAttribute("buttonStatus", false);
        model.addAttribute("invoiceHasLabTest", invoiceHasLabTests);
        model.addAttribute("addLabTestWorkStatus", true);
        return "/labTest/labTestList";
    }

    //result enter form give to front
    @RequestMapping(value = "/lab/labTestResultEnterForm/{id}", method = RequestMethod.GET)
    public String labTestResultEnterForm(@PathVariable("id") Integer id, Model model) {
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findById(id);
        model.addAttribute("addStatus", true);
        model.addAttribute("invoiceHasLabTest", invoiceHasLabTest);
        model.addAttribute("patientAge", dateTimeAgeService.getAge(invoiceHasLabTest.getInvoice().getPatient().getDateOfBirth()));

        return "/labTest/labTestResult";
    }

    // need to save result
    @RequestMapping(value = "/lab/saveResultPatient", method = RequestMethod.POST)
    public String saveLabTestResult(@ModelAttribute SearchProcess searchProcess) {
        //take last lab test result table id
        int lastResultTableId;
        if (resultTableService.findLastResult() != null) {
            lastResultTableId = resultTableService.findLastResult().getId();
        } else {
            lastResultTableId = 0;
        }
// all thing taken from invoice process
        // lab test parameter set without parameter header
        List<LabTestParameter> labTestParameters = searchProcess.getLabTestParameters();
        // all result
        List<String> result = searchProcess.getResult();
        // all absolute count
        List<String> absoluteCount = searchProcess.getAbsoluteCount();
        // invoiceHasLabTest.number = lab reference
        int labReference = Integer.parseInt(searchProcess.getNumber());

// to verify lab test result take invoiceHasLabTest
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findByNumber(labReference);

// take lab test parameter list without parameter header
        List<LabTestParameter> labTestParameterListWithoutHeader = invoiceHasLabTest.getLabTest().getLabTestParameters()
                .stream()
                .filter(labTestParameter -> labTestParameter.getParameterHeader().equals(ParameterHeader.No))
                .collect(Collectors.toList());
// result table create new result  to save
        ResultTable resultTable = new ResultTable();

        if (labTestParameterListWithoutHeader.equals(labTestParameters)) {
            int resultIndex = 0;

            for (LabTestParameter labTestParameter : labTestParameterListWithoutHeader) {
                resultTable.setId(lastResultTableId + 1);
                resultTable.setInvoiceHasLabTest(invoiceHasLabTest);
                resultTable.setLabTest(invoiceHasLabTest.getLabTest());
                resultTable.setResult(result.get(resultIndex));
//if lab test parameter is equal
                if (labTestParameter.equals(labTestParameters.get(resultIndex))) {
                    resultTable.setLabTestParameter(labTestParameters.get(resultIndex));
                }
// there is absolute count value is belongs to FBC and WBC
                if (invoiceHasLabTest.getLabTest().getId() == 311 || invoiceHasLabTest.getLabTest().getId() == 287 && resultIndex > 0) {
                    if (resultIndex <= absoluteCount.size()) {
                        resultTable.setAbsoluteCount(absoluteCount.get(resultIndex - 1));
                    } else {
                        resultTable.setAbsoluteCount(null);
                    }
                }
                resultTableService.persist(resultTable);
                resultIndex++;
                lastResultTableId++;
            }
        }
        invoiceHasLabTest.setLabTestStatus(LabTestStatus.RESULTENTER);
        invoiceHasLabTest.setResultEnteredUser(userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()));
        invoiceHasLabTest.setResultEnteredDateTime(dateTimeAgeService.getCurrentDateTime());
        invoiceHasLabTest.setComment(searchProcess.getComment());

        invoiceHasLabTestService.persist(invoiceHasLabTest);

        return "redirect:/lab/worksheetPrinted";
    }


    // need to give list of patient which is authorized their report
    @RequestMapping(value = "/lab/authorize/resultAuthorizeList", method = RequestMethod.GET)
    public String resultAuthorizedList(Model model) {
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByLabTestState(LabTestStatus.RESULTENTER);
        model.addAttribute("addLabTestResultEnter", true);
        model.addAttribute("invoiceHasLabTest", invoiceHasLabTests);
        model.addAttribute("buttonStatus", false);

        return "/labTest/labTestList";
    }

    // result authorize from
    @RequestMapping(value = "/lab/authorize/labTestAuthorizedForm/{id}", method = RequestMethod.GET)
    public String labTestAuthorizedForm(@PathVariable("id") Integer id, Model model) {
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findById(id);
        model.addAttribute("addStatus", false);
        model.addAttribute("invoiceHasLabTest", invoiceHasLabTest);
        model.addAttribute("patientAge", dateTimeAgeService.getAge(invoiceHasLabTest.getInvoice().getPatient().getDateOfBirth()));
        return "/labTest/labTestResult";
    }

    // Authorization is  ok - lab Test
    @RequestMapping(value = "/lab/authorize/saveAuthorized", method = RequestMethod.POST)
    public String saveAuthorized(@ModelAttribute SearchProcess searchProcess) {
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findByNumber(Integer.parseInt(searchProcess.getNumber()));
        invoiceHasLabTest.setReportAuthorizeDateTime(dateTimeAgeService.getCurrentDateTime());
        invoiceHasLabTest.setReportAuthorizedUser(userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()));
        invoiceHasLabTest.setLabTestStatus(LabTestStatus.AUTHORIZED);

        if (invoiceHasLabTest.getInvoice().getPatient().getEmail() != null) {
            String message = "Following report is ready " + invoiceHasLabTest.getLabTest().getName() + ". \n available to collect ." +
                    "\n Thanks" +
                    "\n Excellent Health Solution";
            boolean authorizedEmail = emailService.sendPatientRegistrationEmail(invoiceHasLabTest.getInvoice().getPatient().getEmail(), "Your report is ready - (not reply)", message);
            if (authorizedEmail) {
                invoiceHasLabTestService.persist(invoiceHasLabTest);
            }
            invoiceHasLabTestService.persist(invoiceHasLabTest);

        }
        invoiceHasLabTestService.persist(invoiceHasLabTest);

        return "redirect:/lab/authorize/resultAuthorizeList";
    }

    // Authorization is  not - lab Test
    @RequestMapping(value = "/lab/authorize/rejectAuthorized/{id}", method = RequestMethod.GET)
    public String rejectAuthorized(@PathVariable("id") Integer id) {
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findById(id);
        invoiceHasLabTest.setResultEnteredDateTime(null);
        invoiceHasLabTest.setResultEnteredUser(null);
        invoiceHasLabTest.setLabTestStatus(LabTestStatus.WORKSHEET);
        invoiceHasLabTestService.persist(invoiceHasLabTest);

        return "redirect:/lab/authorize/resultAuthorizeList";
    }

    // result authorized patient display
    @RequestMapping(value = "/lab/afterResultAuthorizeList", method = RequestMethod.GET)
    public String afterResultAuthorizedList(Model model) {
        HashSet<Invoice> invoices = new HashSet<>();
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTestService.findByLabTestState(LabTestStatus.AUTHORIZED)) {
            // add patient to array list
            invoices.add(invoiceHasLabTest.getInvoice());
        }
        model.addAttribute("noNeed", false);
        model.addAttribute("addLabTestAuthorized", true);
        model.addAttribute("invoiceHasLabTest", invoices);
        model.addAttribute("buttonStatus", false);

        return "/labTest/labTestList";
    }

    //need to show ready to print report and also need to need to authorized, result enter, not sample collect and out side send sample report
    // can used to find patient all report


    private void commonMethodToPatientReportDetails(Model model, List<InvoiceHasLabTest> invoiceHasLabTests) {
        List<InvoiceHasLabTest> sampleCollectAndNotDone = invoiceHasLabTests
                .stream()
                .filter(x -> x.getLabTestStatus().equals(LabTestStatus.SAMPLECOLLECT) && x.getLabTest().getLabtestDoneHere().equals(LabtestDoneHere.NO))
                .collect(Collectors.toList());
        List<InvoiceHasLabTest> sampleCollectLabTest = invoiceHasLabTests
                .stream()
                .filter(x -> x.getLabTestStatus().equals(LabTestStatus.SAMPLECOLLECT))
                .collect(Collectors.toList());
        List<InvoiceHasLabTest> workSheetTake = invoiceHasLabTests
                .stream()
                .filter(x -> x.getLabTestStatus().equals(LabTestStatus.WORKSHEET))
                .collect(Collectors.toList());
        List<InvoiceHasLabTest> resultEnter = invoiceHasLabTests
                .stream()
                .filter(x -> x.getLabTestStatus().equals(LabTestStatus.RESULTENTER))
                .collect(Collectors.toList());
        List<InvoiceHasLabTest> authorized = invoiceHasLabTests
                .stream()
                .filter(x -> x.getLabTestStatus().equals(LabTestStatus.AUTHORIZED))
                .collect(Collectors.toList());
        List<InvoiceHasLabTest> printed = invoiceHasLabTests
                .stream()
                .filter(x -> x.getLabTestStatus().equals(LabTestStatus.PRINTED))
                .collect(Collectors.toList());

        model.addAttribute("invoiceHasLabTest", invoiceHasLabTests);
        model.addAttribute("sampleCollectAndNotDone", sampleCollectAndNotDone);
        model.addAttribute("sampleCollectLabTest", sampleCollectLabTest);
        model.addAttribute("workSheetTake", workSheetTake);
        model.addAttribute("resultEnter", resultEnter);
        model.addAttribute("authorized", authorized);
        model.addAttribute("printed", printed);
    }

    @RequestMapping(value = "/lab/labTestPrintForm/{id}", method = RequestMethod.GET)
    public String labTestPrintFrom(@PathVariable("id") Integer id, Model model) {
//only display within three months report summary
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByInvoiceAndCreatedAtIsBetween(dateTimeAgeService.getPastDateByMonth(3), dateTimeAgeService.getCurrentDate(), invoiceService.findById(id));

        commonMethodToPatientReportDetails(model, invoiceHasLabTests);

        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("invoice1", true);
        model.addAttribute("toPrint", true);
        return "/labTest/printReport";
    }

    //need to show printed list with need to re print button
    @RequestMapping(value = "/lab/print/{id}", method = RequestMethod.GET)
    public String reportPrint(@PathVariable("id") Integer id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findById(id);
        invoiceHasLabTest.setReportPrintedDateTime(dateTimeAgeService.getCurrentDateTime());
        invoiceHasLabTest.setReportPrintedUser(userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()));
        int labRef = invoiceHasLabTestService.persist(invoiceHasLabTest).getId();

        boolean isFlag = labTestService.createPdf(invoiceHasLabTest, context);

        if (isFlag) {
            //request.getRequestURI().toString()
            //redirectAttributes.addFlashAttribute("fileName", );


            String fullPath = request.getServletContext().getRealPath("/resources/report/" + invoiceHasLabTest.getNumber() + ".pdf");
            return "redirect:/invoiceProcess/text";

        }
        return "redirect:/lab/afterResultAuthorizeList";
    }

    // create search form
    @RequestMapping(value = "/lab/searchReportFrom", method = RequestMethod.GET)
    public String SearchFrom(Model model) {
        model.addAttribute("searchReport", new SearchProcess());
        return "/labTest/searchFrom";
    }

    // can used to find patient all report
    @RequestMapping(value = "/lab/searchReport", method = RequestMethod.POST)
    public String labTestDetailsFind(@ModelAttribute SearchProcess searchProcess, Model model, RedirectAttributes redirectAttributes) {
        String pNic = searchProcess.getNic();
        String pNumber = searchProcess.getNumber();

        List<InvoiceHasLabTest> invoiceHasLabTests = new ArrayList<>();
        String message;
        Patient patient;
        //search by patient NIc
        if (!pNic.isEmpty() || !pNumber.isEmpty()) {
            if (!pNic.isEmpty()) {
                patient = patientService.findByNIC(pNic);
                message = "There is no any report given NIC number: " + pNic + ".";

            } else {
                patient = patientService.findByNumber(pNumber);
                message = "There is no any report given Patient's number: " + pNumber + ".";

            }
            if (patient == null) {
                redirectAttributes.addFlashAttribute("searchLab", true);
                redirectAttributes.addFlashAttribute("searchText", message);
                return "redirect:/lab/searchReportFrom";
            }
            // collect all invoice
            List<Invoice> invoices = invoiceService.findByPatient(patient);
            for (Invoice invoice : invoices) {
                invoiceHasLabTests.addAll(invoiceHasLabTestService.findByInvoice(invoice));
            }

        } else {
            //search invoice id
            message = "There is no any report given invoice number: " + searchProcess.getId() + ".";
            invoiceHasLabTests = invoiceHasLabTestService.findByInvoice(invoiceService.findByNumber(searchProcess.getId()));
        }

        // if there is any lab test on given details following method give alert to front
        if (invoiceHasLabTests.isEmpty()) {
            redirectAttributes.addFlashAttribute("searchLab", true);
            redirectAttributes.addFlashAttribute("searchText", message);
            return "redirect:/lab/searchReportFrom";
        }

        commonMethodToPatientReportDetails(model, invoiceHasLabTests);

        return "/labTest/printReport";
    }


    // give authority to make refund form
    @RequestMapping(value = "/lab/authorize/refund", method = RequestMethod.GET)
    public String giveAuthorityRefund() {
        return "/labTest/refundForm";
    }

    // give authority to make refund save
    @RequestMapping(value = "/lab/authorize/refund/save", method = RequestMethod.POST)
    public String giveAuthoritiesRefundSave(SearchProcess searchProcess, Model model) {
        System.out.println(searchProcess.toString());
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findByNumber(Integer.parseInt(searchProcess.getNumber()));

        if (!invoiceHasLabTest.getLabTestStatus().equals(LabTestStatus.WORKSHEET)) {
            //redirectAttributes.addFlashAttribute("unableMLT1", true);
            model.addAttribute("unableMLT1", true);
            return "/labTest/refundForm";
        }

        invoiceHasLabTest.setResultEnteredDateTime(null);
        invoiceHasLabTest.setResultEnteredUser(null);
        invoiceHasLabTest.setLabTestStatus(LabTestStatus.NOSAMPLE);
        invoiceHasLabTest.setComment(searchProcess.getComment());
// this may help to find at what time mlt 1 give authority to refund
        invoiceHasLabTest.setReportAuthorizeDateTime(dateTimeAgeService.getCurrentDateTime());
        invoiceHasLabTestService.persist(invoiceHasLabTest);

        return "redirect:/";
    }

    @GetMapping("/resultPrint")
    public String showText() {
        return "printView/resultPrint";
    }
}
