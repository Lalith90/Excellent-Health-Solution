package lk.solution.health.excellent.lab.service;

import lk.solution.health.excellent.util.interfaces.AbstractService;
import lk.solution.health.excellent.lab.dao.ResultTableDao;
import lk.solution.health.excellent.lab.entity.ResultTable;
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
public class ResultTableService implements AbstractService<ResultTable, Integer> {

    private final ResultTableDao resultTableDao;

    @Autowired
    public ResultTableService(ResultTableDao resultTableDao) {
        this.resultTableDao = resultTableDao;
    }

    @Cacheable(value = "resultTable")
    public List<ResultTable> findAll() {
        System.out.println("result table cache ok");
        return resultTableDao.findAll();
    }

    @CachePut(value = "resultTable")
    public ResultTable findById(Integer id) {
        return resultTableDao.getOne(id);
    }

    @CachePut(value = "resultTable")
    @Transactional
    public ResultTable persist(ResultTable resultTable) {
        return resultTableDao.save(resultTable);
    }


    @Transactional
    @CacheEvict(value = "resultTable")
    public boolean delete(Integer id) {
        resultTableDao.deleteById(id);
        return false;
    }

    @CachePut(value = "resultTable")
    public List<ResultTable> search(ResultTable resultTable) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<ResultTable> resultTableExample = Example.of(resultTable, matcher);
        return resultTableDao.findAll(resultTableExample);
    }

    @Transactional
    public ResultTable findLastResult(){
        return resultTableDao.findFirstByOrderByIdDesc();
    }
}
