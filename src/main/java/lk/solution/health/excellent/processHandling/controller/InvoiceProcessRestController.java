package lk.solution.health.excellent.processHandling.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.service.LabTestService;
import lk.solution.health.excellent.resource.entity.Patient;
import lk.solution.health.excellent.resource.service.MedicalPackageService;
import lk.solution.health.excellent.resource.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/invoiceProcess")
public class InvoiceProcessRestController {

    private final PatientService patientService;
    private final LabTestService labTestService;
    private final MedicalPackageService medicalPackageService;

    @Autowired
    public InvoiceProcessRestController(PatientService patientService, LabTestService labTestService, MedicalPackageService medicalPackageService) {
        this.patientService = patientService;
        this.labTestService = labTestService;
        this.medicalPackageService = medicalPackageService;
    }


    // using spring boot filtering service most required variable send to the front end
    private void filterParameter(MappingJacksonValue mappingJacksonValue, FilterProvider filters) {
        mappingJacksonValue.setFilters(filters);
    }

    // medical package lab test send to fonts end
    @GetMapping("/medicalPackageLabTestGet/{id}")
    public MappingJacksonValue getLabTestByMedicalPackageId(@PathVariable Integer id) {
        List<LabTest> labTests = medicalPackageService.findById(id).getLabTests();
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(labTests);
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("code", "name", "labtestDoneHere");
        FilterProvider filters = new SimpleFilterProvider().addFilter("LabTest", simpleBeanPropertyFilter);
        filterParameter(mappingJacksonValue, filters);
        return mappingJacksonValue;
    }

    // medical package send to fonts end
    @GetMapping("/medicalPackageGet/{id}")
    public MappingJacksonValue getMedicalPackageById(@PathVariable Integer id) {
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(medicalPackageService.findById(id));
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "price");
        FilterProvider filters = new SimpleFilterProvider().addFilter("MedicalPackage", simpleBeanPropertyFilter);
        filterParameter(mappingJacksonValue, filters);
        return mappingJacksonValue;
    }

    //send patient details  send to front end
    @GetMapping("/patientFind")
    public MappingJacksonValue getPatient(@PathParam("Patient") Patient patient) {
        //Get All Patient
        List<Patient> patients = patientService.findAll();

        List<Patient> patientList = new ArrayList<>();
        if (patient.getNumber() != null) {
            patientList.add(patientService.findByNumber(patient.getNumber()));
        }
        if (patient.getNic() != null) {
            patientList.add(patientService.findByNIC(patient.getNic()));
        }
        if (patient.getMobile() != null) {
            patientList.addAll(patientService.findByMobile(patient.getMobile()));
        }
        if (patientList.isEmpty()) {
            patientList.addAll(patientService.search(patient));
        }

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(patientList);
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "number", "title", "name", "gender", "nic", "dateOfBirth", "email", "mobile", "land");
        FilterProvider filters = new SimpleFilterProvider().addFilter("Patient", simpleBeanPropertyFilter);
        filterParameter(mappingJacksonValue, filters);
        return mappingJacksonValue;
    }

    //if need to add entity variable to url add @PathParam to class
    @GetMapping(value = "/searchLabTest1")
    public MappingJacksonValue search1(@PathParam("LabTest") LabTest labTest) {
        //LabTest labTest2 = new LabTest();
        //labTest2.setName("lipid");
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(labTestService.search(labTest));
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name");
        FilterProvider filters = new SimpleFilterProvider().addFilter("LabTest", simpleBeanPropertyFilter);
        filterParameter(mappingJacksonValue, filters);
        return mappingJacksonValue;
    }


    //@RequestMapping(value = "/search", method = RequestMethod.GET)
    @GetMapping("/labTest1/{id}")
    public MappingJacksonValue searchLabTest(@PathVariable Integer id) {
        LabTest labTest = new LabTest();
        //labTest.setName("aa");
        labTest.setId(id);
        List<LabTest> labTests = labTestService.search(labTest);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(labTests);
        for (LabTest lab : labTests) {
            SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "labtestDoneHere");
            FilterProvider filters = new SimpleFilterProvider().addFilter("LabTest", simpleBeanPropertyFilter);

            filterParameter(mappingJacksonValue, filters);
        }

        return mappingJacksonValue;
    }

    @GetMapping("/labTest11/{id}")
    public LabTest searchTest(@PathVariable Integer id) {
        return labTestService.findById(id);
    }


    public MappingJacksonValue getAll() {
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(labTestService.findAll());
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "description", "labtestDoneHere");
        FilterProvider filters = new SimpleFilterProvider().addFilter("LabTest", simpleBeanPropertyFilter);
        filterParameter(mappingJacksonValue, filters);


        return mappingJacksonValue;
    }
   /*
        @ResponseBody
        public MappingJacksonValue getOne(@PathVariable Integer id){


                MappingJacksonValue mappingJacksonValue =  new MappingJacksonValue(labTestService.findById(id));
                SimpleBeanPropertyFilter simpleBeanPropertyFilter =  SimpleBeanPropertyFilter.filterOutAllExcept("id","name","price","labtestDoneHere");
                FilterProvider filters = new SimpleFilterProvider().addFilter("LabTest", simpleBeanPropertyFilter);

                filterParameter(mappingJacksonValue,filters);
            System.out.println(mappingJacksonValue.toString().chars());

            return mappingJacksonValue;


        }
        @GetMapping("/labTest1")*/

//    @RequestMapping(value="/pat", method = RequestMethod.GET)
//    public List<Patient> findAll(@RequestParam(name = "name",required = false) String name){
//        System.out.println("first-part");
//        if(StringUtils.isEmpty(name)){
//            System.out.println("second - part");
//            return Collections.singletonList(patientService.findByName(name));
//        }
//        System.out.println("all user");
//        return patientService.findAll();
//    }


//        @GetMapping("/labTest1/{id}")
//        public ResponseEntity<Object> findLabTest1(@PathVariable Integer id){
//            System.out.println(id);
//            ServiceResponse<LabTest> response = new ServiceResponse<>("success", labTestService.findById(id));
//            return new ResponseEntity<>(response, HttpStatus.OK);
//    }


/*    @GetMapping("/labTest1/{id}")
//    @ResponseBody --> this will give JSON object representation
    public LabTest findLabTest1(@PathVariable Integer id){
        System.out.println(id);
        return labTestService.findById(id);
    }*/

//    @GetMapping("/lat")
//    public List<LabTest> getLabTest() {
//        return labTestService.findAll();
//    }
    /* @PostMapping("/api/search")
    public ResponseEntity<?> getSearchResultViaAjax(
            @Valid @RequestBody SearchCriteria search, Errors errors) {

        AjaxResponseBody result = new AjaxResponseBody();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {

            result.setMsg(errors.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));

            return ResponseEntity.badRequest().body(result);

        }

        List<User> users = userService.findByUserNameOrEmail(search.getUsername());
        if (users.isEmpty()) {
            result.setMsg("no user found!");
        } else {
            result.setMsg("success");
        }
        result.setResult(users);

        return ResponseEntity.ok(result);

    }*/


}
