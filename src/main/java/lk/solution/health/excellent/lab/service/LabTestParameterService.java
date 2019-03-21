package lk.solution.health.excellent.lab.service;

import lk.solution.health.excellent.common.interfaces.AbstractService;
import lk.solution.health.excellent.lab.dao.LabTestParameterDao;
import lk.solution.health.excellent.lab.entity.LabTestParameter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabTestParameterService implements AbstractService<LabTestParameter, Integer> {

    private LabTestParameterDao labtestParameterDao;

    public LabTestParameterService(LabTestParameterDao labtestParameterDao){
        this.labtestParameterDao = labtestParameterDao;
    }


    @Cacheable(value = "labtestParameter")
    public List<LabTestParameter> findAll() {
        System.out.println("Lab Test paameter cache ok");
        return labtestParameterDao.findAll();
    }

    @Cacheable(value = "labtestParameter", key = "#id")
    public LabTestParameter findById(Integer id) {
        return labtestParameterDao.getOne(id);
    }

    @CachePut(value = "labtestParameter", key = "#id")
    @Transactional
    public LabTestParameter persist(LabTestParameter labtestParameter) {
        return labtestParameterDao.save(labtestParameter);
    }

    @CacheEvict(value = "labtestParameter", key = "#id")
    public boolean delete(Integer id) {
        labtestParameterDao.deleteById(id);
        return false;
    }

    @CachePut(value = "labtestParameter", key = "#id")
    public List<LabTestParameter> search(LabTestParameter labTestParameter) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<LabTestParameter> labTestParameterExample = Example.of(labTestParameter, matcher);
        return labtestParameterDao.findAll(labTestParameterExample);
    }

    public LabTestParameter findByCode(String fbs1) {
        return labtestParameterDao.findByCode(fbs1);
    }

/*    public LabTestParameter findByCodeAndResult(String code, String result) {
        return labtestParameterDao.findByCodeAndResultGreaterThan(code, result);
    }*/
}
