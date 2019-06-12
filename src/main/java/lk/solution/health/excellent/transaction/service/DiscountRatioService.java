package lk.solution.health.excellent.transaction.service;

import lk.solution.health.excellent.util.interfaces.AbstractService;
import lk.solution.health.excellent.transaction.dao.DiscountRatioDao;
import lk.solution.health.excellent.transaction.entity.DiscountRatio;
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
@Transactional
public class DiscountRatioService implements AbstractService<DiscountRatio, Integer> {

    private DiscountRatioDao discountRatioDao;

    @Autowired
    public DiscountRatioService(DiscountRatioDao discountRatioDao){
        this.discountRatioDao = discountRatioDao;
    }

    @Cacheable(value = "discount")
    public List<DiscountRatio> findAll() {
        return discountRatioDao.findAll();
    }

    @CachePut(value = "discountRatio")
    public DiscountRatio findById(Integer id) {
        return discountRatioDao.getOne(id);
    }

    @CachePut(value = "discountRatio")
    public DiscountRatio persist(DiscountRatio discountRatio) {
        return discountRatioDao.save(discountRatio);
    }

    @CacheEvict(value = "discountRatio")
    public boolean delete(Integer id) {
        discountRatioDao.deleteById(id);
        return false;
    }

    @CachePut(value = "discountRatio")
    public List<DiscountRatio> search(DiscountRatio discountRatio) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<DiscountRatio> discountRatioExample = Example.of(discountRatio, matcher);
        return discountRatioDao.findAll(discountRatioExample);
    }
}
