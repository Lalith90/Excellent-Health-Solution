package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.entity.Enum.LabTestStatus;
import lk.solution.health.excellent.lab.entity.Enum.LabtestDoneHere;
import lk.solution.health.excellent.lab.entity.ResultTable;
import lk.solution.health.excellent.lab.service.LabTestParameterService;
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
import lk.solution.health.excellent.util.service.FileHandelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LabProcessController {

    private final InvoiceHasLabTestService invoiceHasLabTestService;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final DateTimeAgeService dateTimeAgeService;
    private final LabTestService labTestService;
    private final LabTestParameterService labTestParameterService;
    private final PatientService patientService;
    private final FileHandelService fileHandelService;
    private final ResultTableService resultTableService;
    private final ServletContext context;
    private final EmailService emailService;

    @Autowired
    public LabProcessController(InvoiceHasLabTestService invoiceHasLabTestService, UserService userService, InvoiceService invoiceService, DateTimeAgeService dateTimeAgeService, LabTestService labTestService, LabTestParameterService labTestParameterService, PatientService patientService, FileHandelService fileHandelService, ResultTableService resultTableService, ServletContext context, EmailService emailService) {
        this.invoiceHasLabTestService = invoiceHasLabTestService;
        this.userService = userService;
        this.invoiceService = invoiceService;
        this.dateTimeAgeService = dateTimeAgeService;
        this.labTestService = labTestService;
        this.labTestParameterService = labTestParameterService;
        this.patientService = patientService;
        this.fileHandelService = fileHandelService;
        this.resultTableService = resultTableService;
        this.context = context;
        this.emailService = emailService;
    }

    //Sample Collected Investigation List for taken work sheet
    @RequestMapping(value = "/lab/sampleCollect", method = RequestMethod.GET)
    public String sampleCollectedPatient(Model model) {
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.
                findByLabTestState(LabTestStatus.SAMPLECOLLECT)
                .stream()
                .filter((x) -> x.getLabTest().getLabtestDoneHere().equals(LabtestDoneHere.YES))
                .collect(Collectors.toList());
invoiceHasLabTests.forEach(System.out::println);
        model.addAttribute("invoiceHasLabTest", invoiceHasLabTests);
        model.addAttribute("addLabSampleCollect", true);
        model.addAttribute("buttonStatus", true);
        model.addAttribute("inputStatus", true);

        return "/labTest/labTestList";
    }

    //save Worksheet printed lab test
    @RequestMapping(value = "/lab/saveWorkSheetPatient", method = RequestMethod.POST)
    public String saveWorksheetTakenPatient(@ModelAttribute SearchProcess searchProcess) {

        List<InvoiceHasLabTest> invoiceHasLabTests = searchProcess.getInvoiceHasLabTests();
        //TODO
        // any how need to find how to print pdf through web browser using java servlet
        // NEED TO CREATE WORK SHEET USING I-TEXT BUDDY --> //USING INVOICE NUMBER AND LAB TEST LIST

        //all work sheet taken test add to list
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests) {
            invoiceHasLabTest.setLabTestStatus(LabTestStatus.WORKSHEET);
            invoiceHasLabTest.setWorkSheetTakenUser(userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()));
            invoiceHasLabTest.setWorkSheetTakenDateTime(dateTimeAgeService.getCurrentDateTime());
        }
        invoiceHasLabTestService.persistBulk(invoiceHasLabTests);
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
    public String saveLabTestResult(@ModelAttribute ResultTable searchProcess) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("result save " + searchProcess.toString());
//todo --> save result to lab test result table



       /* // find last result id from result table
        Integer id = resultTableService.findLastResult().getId() + 1;

        ResultTable resultTable = new ResultTable();

        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findById(searchProcess.getId());

        // List<String> codes = searchProcess.getCode1();
        List<String> results = searchProcess.getResult();


        for (LabTestParameter labTestParameter : invoiceHasLabTest.getLabTest().getLabTestParameters()) {

            resultTable.setId(id);
            resultTable.setInvoiceHasLabTest(invoiceHasLabTest);
            resultTable.setLabTest(invoiceHasLabTest.getLabTest());
            resultTable.setLabTestParameter(labTestParameter);
            for (String result : results) {


                System.out.println(labTestParameter.getName() + "\n parameter name");

                resultTable.setResult(result);

                System.out.println(result + "\n entered result for relevant filed");


            }
            resultTableService.persist(resultTable);
            id++;
            System.out.println(id + " id for next");
        }
        invoiceHasLabTest.setResultEnteredDateTime(dateTimeAgeService.getCurrentDateTime());
        invoiceHasLabTest.setResultEnteredUser(userService.findByUserName(authentication.getName()));
        invoiceHasLabTest.setLabTestStatus(LabTestStatus.RESULTENTER);
        invoiceHasLabTestService.persist(invoiceHasLabTest);
        // to check the save result
        */
       /*System.out.println("to check the save result");
            for (LabTestParameter labTestParameter: invoiceHasLabTest.getLabTest().getLabTestParameters()){
                System.out.println(labTestParameter.getName());
                System.out.println(labTestParameter.getResult());
            }*/
        return "redirect:/lab/worksheetPrinted";
    }


    // need to give list of patient who is authorized their report
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
        //BEFORE SEND THE DATA CHECK LAB TEST NAME FULL BLOOD COUNT OR = it ok done 12/11/2018
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findById(id);
        model.addAttribute("addStatus", false);
        model.addAttribute("invoiceHasLabTest", invoiceHasLabTest);
        model.addAttribute("patientAge", dateTimeAgeService.getAge(invoiceHasLabTest.getInvoice().getPatient().getDateOfBirth()));
        return "/labTest/labTestResult";
    }

    // Authorization is  ok - lab Test
    @RequestMapping(value = "/lab/authorize/saveAuthorized", method = RequestMethod.POST)
    public String saveAuthorized(@ModelAttribute SearchProcess searchProcess) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findById(searchProcess.getId());
        //System.out.println("Authorized save");
        invoiceHasLabTest.setReportAuthorizeDateTime(dateTimeAgeService.getCurrentDateTime());
        invoiceHasLabTest.setReportAuthorizedUser(userService.findByUserName(authentication.getName()));
        invoiceHasLabTest.setLabTestStatus(LabTestStatus.AUTHORIZED);

        if (invoiceHasLabTest.getInvoice().getPatient().getEmail() != null) {
            String message = "Following report is ready " + invoiceHasLabTest.getLabTest().getName() + ". \n Now you can collect it." +
                    "\n Thanks" +
                    "\n Excellent Health Solution";
            boolean authorizedEmail = emailService.sendPatientRegistrationEmail(invoiceHasLabTest.getInvoice().getPatient().getEmail(), "Your report is ready", message);
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findById(id);
        System.out.println("rejected Save");
        invoiceHasLabTest.setResultEnteredDateTime(dateTimeAgeService.getCurrentDateTime());
        invoiceHasLabTest.setResultEnteredUser(userService.findByUserName(authentication.getName()));
        invoiceHasLabTest.setLabTestStatus(LabTestStatus.RESULTENTER);
        invoiceHasLabTestService.persist(invoiceHasLabTest);

        return "redirect:/lab/authorize/resultAuthorizeList";
    }

    // result authorized patient display
    @RequestMapping(value = "/lab/afterResultAuthorizeList", method = RequestMethod.GET)
    public String afterResultAuthorizedList(Model model) {
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByLabTestState(LabTestStatus.AUTHORIZED);
        List<Invoice> invoices = new ArrayList<>();
        for (InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTests) {
            //System.out.println();
            // add patient to array list
            invoices.add(invoiceHasLabTest.getInvoice());
        }
        //remove duplicate from he list
        List<Invoice> noDuplicateInvoice = invoices.stream()
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("noNeed", false);
        model.addAttribute("addLabTestAuthorized", true);
        model.addAttribute("invoiceHasLabTest", noDuplicateInvoice);
        model.addAttribute("buttonStatus", false);

        return "/labTest/labTestList";
    }

    //need to show ready to print report and also need to need to authorized, result enter, not sample collect and out side send sample report
    // can used to find patient all report
    @RequestMapping(value = "/lab/labTestPrintForm/{id}", method = RequestMethod.GET)
    public String labTestPrintFrom(@PathVariable("id") Integer id, Model model) {
        List<InvoiceHasLabTest> invoiceHasLabTests = invoiceHasLabTestService.findByInvoice(invoiceService.findById(id));

        List<InvoiceHasLabTest> sampleCollectAndNotDone = new ArrayList<>();
        List<InvoiceHasLabTest> sampleCollectLabTest = new ArrayList<>();
        List<InvoiceHasLabTest> workSheetTake = new ArrayList<>();
        List<InvoiceHasLabTest> resultEnter = new ArrayList<>();
        List<InvoiceHasLabTest> authorized = new ArrayList<>();
        List<InvoiceHasLabTest> printed = new ArrayList<>();

        for (InvoiceHasLabTest hasLabTestList : invoiceHasLabTests) {

            if (LabTestStatus.SAMPLECOLLECT.name().equals(hasLabTestList.getLabTestStatus().name()) && hasLabTestList.getLabTest().getLabtestDoneHere().name().equals(LabtestDoneHere.NO.name())) {
                sampleCollectAndNotDone.add(hasLabTestList);
            }
            if (LabTestStatus.SAMPLECOLLECT.name().equals(hasLabTestList.getLabTestStatus().name())) {
                sampleCollectLabTest.add(hasLabTestList);
            }
            if (LabTestStatus.WORKSHEET.name().equals(hasLabTestList.getLabTestStatus().name())) {
                workSheetTake.add(hasLabTestList);
            }
            if (LabTestStatus.RESULTENTER.name().equals(hasLabTestList.getLabTestStatus().name())) {
                resultEnter.add(hasLabTestList);
            }
            if (LabTestStatus.AUTHORIZED.name().equals(hasLabTestList.getLabTestStatus().name())) {
                authorized.add(hasLabTestList);
            }
            if (LabTestStatus.PRINTED.name().equals(hasLabTestList.getLabTestStatus().name())) {
                authorized.add(hasLabTestList);
            }
        }

        model.addAttribute("invoiceHasLabTest", invoiceHasLabTests);
        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("invoice1", true);
        model.addAttribute("sampleCollectAndNotDone", sampleCollectAndNotDone);
        model.addAttribute("sampleCollectLabTest", sampleCollectLabTest);
        model.addAttribute("workSheetTake", workSheetTake);
        model.addAttribute("resultEnter", resultEnter);
        model.addAttribute("authorized", authorized);
        model.addAttribute("printed", printed);

        return "/labTest/printReport";
    }

    //need to show printed list with need to re print button
    @RequestMapping(value = "/lab/print/{id}", method = RequestMethod.GET)
    public String reportPrint(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        InvoiceHasLabTest invoiceHasLabTest = invoiceHasLabTestService.findById(id);
        invoiceHasLabTest.setReportPrintedDateTime(dateTimeAgeService.getCurrentDateTime());
        invoiceHasLabTest.setReportPrintedUser(userService.findByUserName(authentication.getName()));
        invoiceHasLabTestService.persist(invoiceHasLabTest);

/*        boolean isFlag = labTestService.createPdf(invoiceHasLabTest, context, request, response);
        System.out.println(isFlag);
        if (isFlag) {
            String fullPath = request.getServletContext().getRealPath("/resources/report/" + invoiceHasLabTest.getInvoice().getPatient().getName() + ".pdf");
            boolean download = fileHandelService.fileDownload(fullPath, response, invoiceHasLabTest.getInvoice().getPatient().getName() + ".pdf");
            if (download){
                System.out.println("download is done");
                return "redirect:/lab/afterResultAuthorizeList";
            }else {
                System.out.println(" file download fail");
                return "redirect:/lab/afterResultAuthorizeList";
            }

        }*/
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
        String invoiceId = searchProcess.getCode();

        List<InvoiceHasLabTest> invoiceHasLabTests = new ArrayList<>();
        String message = "You did not enter any search criteria. Do not Jok with me.";

        //search by patient NIc
        if (!pNic.isEmpty() || !pNumber.isEmpty()) {
            Patient patient = null;
            if (!pNic.isEmpty()) {
                patient = patientService.findByNIC(pNic);
                message = "There is no any report given NIC number: " + pNic + ".";
                pNumber = "";
                invoiceId = "";

            } else {
                //search by patient's Number
                message = "There is no any report given Patient's number: " + pNumber + ".";
                System.out.println(patient);
                pNic = "";
                invoiceId = "";

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
            Integer billedNumber = Integer.parseInt(invoiceId);
            //search invoice id
            message = "There is no any report given invoice number: " + invoiceId + ".";
            invoiceHasLabTests = invoiceHasLabTestService.findByInvoice(invoiceService.findById(billedNumber));
        }


        // if there is any lab test on given details following method give alert to front
        if (invoiceHasLabTests.isEmpty() || pNumber.isEmpty() && pNic.isEmpty() && invoiceId.isEmpty()) {
            redirectAttributes.addFlashAttribute("searchLab", true);
            redirectAttributes.addFlashAttribute("searchText", message);
            return "redirect:/lab/searchReportFrom";
        }


        List<InvoiceHasLabTest> sampleCollectAndNotDone = new ArrayList<>();
        List<InvoiceHasLabTest> sampleCollectLabTest = new ArrayList<>();
        List<InvoiceHasLabTest> workSheetTake = new ArrayList<>();
        List<InvoiceHasLabTest> resultEnter = new ArrayList<>();
        List<InvoiceHasLabTest> authorized = new ArrayList<>();
        List<InvoiceHasLabTest> printed = new ArrayList<>();

        for (InvoiceHasLabTest hasLabTestList : invoiceHasLabTests) {

            if (LabTestStatus.SAMPLECOLLECT.name().equals(hasLabTestList.getLabTestStatus().name()) && hasLabTestList.getLabTest().getLabtestDoneHere().name().equals(LabtestDoneHere.NO.name())) {
                sampleCollectAndNotDone.add(hasLabTestList);
            }
            if (LabTestStatus.SAMPLECOLLECT.name().equals(hasLabTestList.getLabTestStatus().name())) {
                sampleCollectLabTest.add(hasLabTestList);
            }
            if (LabTestStatus.WORKSHEET.name().equals(hasLabTestList.getLabTestStatus().name())) {
                workSheetTake.add(hasLabTestList);
            }
            if (LabTestStatus.RESULTENTER.name().equals(hasLabTestList.getLabTestStatus().name())) {
                resultEnter.add(hasLabTestList);
            }
            if (LabTestStatus.AUTHORIZED.name().equals(hasLabTestList.getLabTestStatus().name())) {
                authorized.add(hasLabTestList);
            }
            if (LabTestStatus.PRINTED.name().equals(hasLabTestList.getLabTestStatus().name())) {
                authorized.add(hasLabTestList);
            }
        }

        model.addAttribute("invoiceHasLabTest", invoiceHasLabTests);
        model.addAttribute("invoice1", false);
        model.addAttribute("sampleCollectAndNotDone", sampleCollectAndNotDone);
        model.addAttribute("sampleCollectLabTest", sampleCollectLabTest);
        model.addAttribute("workSheetTake", workSheetTake);
        model.addAttribute("resultEnter", resultEnter);
        model.addAttribute("authorized", authorized);
        model.addAttribute("printed", printed);

        return "/labTest/printReport";
    }

}
