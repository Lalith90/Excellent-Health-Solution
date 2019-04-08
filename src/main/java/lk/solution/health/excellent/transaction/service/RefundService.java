package lk.solution.health.excellent.transaction.service;


import lk.solution.health.excellent.common.interfaces.AbstractService;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.transaction.dao.RefundDao;
import lk.solution.health.excellent.transaction.entity.Refund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RefundService implements AbstractService<Refund, Integer> {
    private final RefundDao refundDao;

    @Autowired
    public RefundService(RefundDao refundDao) {
        this.refundDao = refundDao;
    }

    public List<Refund> findAll() {
        return refundDao.findAll();
    }

    public Refund findById(Integer id) {
        return refundDao.getOne(id);
    }

    @Transactional
    public Refund persist(Refund refund) {
        return refundDao.save(refund);
    }

    public boolean delete(Integer id) {
        refundDao.deleteById(id);
        return false;
    }

    public List<Refund> search(Refund refund) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Refund> refundExample = Example.of(refund, matcher);
        return refundDao.findAll(refundExample);
    }

    public List<Refund> findByUserAndCreatedAt(User user, LocalDate today) {
        return refundDao.findByUserAndCreatedAt(user, today);
    }

    public List<Refund> findByCreatedAt(LocalDate today) {
        return refundDao.findByCreatedAt(today);
    }

    public List<Refund> findByCreatedAtIsBetween(LocalDate from, LocalDate to) {
        return refundDao.findByCreatedAtIsBetween(from, to);
    }
}
