package lk.solution.health.excellent.lab.dao;

import lk.solution.health.excellent.lab.entity.LabTestParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface LabTestParameterDao extends JpaRepository<LabTestParameter, Integer> {
    LabTestParameter findByCode(String fbs1);

   /* LabTestParameter findByCodeAndResultGreaterThan(String code, String result);*/
}
