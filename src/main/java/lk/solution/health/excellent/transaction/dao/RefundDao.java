package lk.solution.health.excellent.transaction.dao;

import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.transaction.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface RefundDao extends JpaRepository<Refund, Integer> {


    List<Refund> findByUserAndCreatedAt(User user, LocalDate today);

    List<Refund> findByCreatedAt(LocalDate today);

    List<Refund> findByCreatedAtIsBetween(LocalDate from, LocalDate to);
}
