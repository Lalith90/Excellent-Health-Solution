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

    @Cacheable(value = "patient")
    public List<Patient> findAll() {
        System.out.println("patient cache ok");
        return patientDao.findAll();
    }

    @Cacheable(value = "patient", key = "#id")
    public Patient findById(Integer id) {
        return patientDao.getOne(id);
    }

    @CachePut(value = "patient", key = "#id")
    @Transactional
    public Patient persist(Patient patient) {
        return patientDao.save(patient);
    }

    @CacheEvict(value = "patient", key = "#id")
    public boolean delete(Integer id) {
        patientDao.deleteById(id);
        return false;
    }

    @Cacheable(value = "patient")
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


    public Patient findByNIC(String nic) {
        return patientDao.findByNic(nic);
    }


}