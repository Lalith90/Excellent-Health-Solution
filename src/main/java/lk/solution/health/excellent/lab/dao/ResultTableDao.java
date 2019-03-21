package lk.solution.health.excellent.lab.dao;

import lk.solution.health.excellent.lab.entity.ResultTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultTableDao extends JpaRepository<ResultTable, Integer> {
    ResultTable findFirstByOrderByIdDesc();
}
