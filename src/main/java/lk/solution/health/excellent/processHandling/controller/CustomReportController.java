package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.general.dao.InvoiceHasLabTestDao;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.lab.service.LabTestParameterService;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.processHandling.helpingClass.SearchProcess;
import lk.solution.health.excellent.resource.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CustomReportController {
private final PatientService patientService;
private final InvoiceHasLabTestService invoiceHasLabTestService;
private final InvoiceHasLabTestDao invoiceHasLabTestDao;
private final LabTestService labTestService;
private final LabTestParameterService labTestParameterService;

    public CustomReportController(PatientService patientService, InvoiceHasLabTestService invoiceHasLabTestService, InvoiceHasLabTestDao invoiceHasLabTestDao, LabTestService labTestService, LabTestParameterService labTestParameterService) {
        this.patientService = patientService;
        this.invoiceHasLabTestService = invoiceHasLabTestService;
        this.invoiceHasLabTestDao = invoiceHasLabTestDao;
        this.labTestService = labTestService;
        this.labTestParameterService = labTestParameterService;
    }
    @RequestMapping(value = "/customReportForm" , method = RequestMethod.GET)
    public String customReportForm(Model model){

        model.addAttribute("searchProcess", new SearchProcess());
        model.addAttribute("parameter", labTestParameterService.findAll());

        return "labTest/customReport";
    }


    /*@RequestMapping(value = "/customReport" , method = RequestMethod.POST)
    public String customReport(@ModelAttribute SearchProcess searchProcess, Model model){
    String code = labTestParameterService.findById(searchProcess.getId()).getCode();
        System.out.println(code);
    String result = searchProcess.getNumber();
        System.out.println(result);
        LabTestParameter parameters = labTestParameterService.findByCode(code);

        List<LabTest> labTests = labTestService.findByLabTestParameter(parameters);



       List<CustomTestFind> customTestFinds = null;

        CustomTestFind customTestFind = new CustomTestFind();


        for(LabTest labTest : labTests){

              for (LabTestParameter labTestParameter : labTest.getLabTestParameters()){
                String result1 =  labTestParameter.getResult();

                Integer result2 = Integer.parseInt(result1);


                if (result2 >= Integer.parseInt(result)){
                    customTestFind.setCount(Integer.parseInt(result));
                    System.out.println("140 greater");
                    for(InvoiceHasLabTest invoiceHasLabTest : invoiceHasLabTestService.findByLabTest(labTest)){
                        customTestFind.setName(invoiceHasLabTest.getInvoice().getPatient().getName());
                        customTestFind.setEmail(invoiceHasLabTest.getInvoice().getPatient().getEmail());
                    }
                    customTestFinds.add(customTestFind);
                }
              }


        }
        model.addAttribute("searchProcess", new SearchProcess());
        model.addAttribute("parameter", customTestFinds);
        model.addAttribute("patients", customTestFinds);
        model.addAttribute("showStatus", true);
        return "labTest/customReport";

    }*/

}
