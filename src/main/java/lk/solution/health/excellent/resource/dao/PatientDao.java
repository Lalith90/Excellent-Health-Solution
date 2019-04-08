package lk.solution.health.excellent.resource.dao;

import lk.solution.health.excellent.resource.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository

public interface PatientDao extends JpaRepository<Patient, Integer> {
    Patient findFirstByOrderByIdDesc();

    Patient findByNic(String nic);

    Patient findByNumber(String number);

    List<Patient> findByMobile(String mobile);
}
