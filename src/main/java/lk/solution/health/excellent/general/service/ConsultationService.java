package lk.solution.health.excellent.general.service;

import lk.solution.health.excellent.common.interfaces.AbstractService;
import lk.solution.health.excellent.general.dao.ConsultationDao;
import lk.solution.health.excellent.general.entity.Consultation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultationService implements AbstractService<Consultation, Integer> {

    private ConsultationDao consultationDao;
    @Autowired
    public ConsultationService(ConsultationDao consultationDao){
        this.consultationDao = consultationDao;
    }


    @Cacheable(value = "Consultation")
    public List<Consultation> findAll() {
        return consultationDao.findAll();
    }


    public Consultation findById(Integer id) {
        return consultationDao.getOne(id);
    }

    @Transactional
    public Consultation persist(Consultation consultation) {
        return consultationDao.save(consultation);
    }

    @Transactional
    public boolean delete(Integer id) {
        consultationDao.deleteById(id);
        return false;
    }


    public List<Consultation> search(Consultation consultation) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Consultation> consultationExample = Example.of(consultation, matcher);
        return consultationDao.findAll(consultationExample);
    }
}
