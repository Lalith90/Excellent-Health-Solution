package lk.solution.health.excellent.lab.dao;

import lk.solution.health.excellent.lab.entity.LabTest;
import lk.solution.health.excellent.lab.entity.LabTestParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository

public interface LabTestDao extends JpaRepository<LabTest, Integer> {

    LabTest findByCode(String code);


    List<LabTest> findByLabTestParameters(LabTestParameter labTestParameter);

}
