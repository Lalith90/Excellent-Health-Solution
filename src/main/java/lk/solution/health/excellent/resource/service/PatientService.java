package lk.solution.health.excellent.resource.service;

import lk.solution.health.excellent.common.interfaces.AbstractService;
import lk.solution.health.excellent.resource.dao.PatientDao;
import lk.solution.health.excellent.resource.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService implements AbstractService<Patient, Integer> {
    private final PatientDao patientDao;

    @Autowired
    public PatientService(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    @Cacheable("patient")
    public List<Patient> findAll() {
        System.out.println("patient cache ok");
        return patientDao.findAll();
    }

    @CachePut(value = "patient")
    public Patient findById(Integer id) {
        return patientDao.getOne(id);
    }

    @CachePut(value = "patient")
    public Patient persist(Patient patient) {
        return patientDao.save(patient);
    }

    @CacheEvict(value = "patient")
    public boolean delete(Integer id) {
        patientDao.deleteById(id);
        return false;
    }

    @CachePut(value = "patient")
    public List<Patient> search(Patient patient) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Patient> patientExample = Example.of(patient, matcher);
        return patientDao.findAll(patientExample);
    }


    public Patient lastPatient() {
        return patientDao.findFirstByOrderByIdDesc();
    }

    @CachePut(value = "patient")
    public Patient findByNIC(String nic) {
        return patientDao.findByNic(nic);
    }

    @CachePut(value = "patient")
    public Patient findByNumber(String number) {
        return patientDao.findByNumber(number);
    }
    @CachePut(value = "patient")
    public List<Patient> findByMobile(String mobile) {
        return patientDao.findByMobile(mobile);
    }
}