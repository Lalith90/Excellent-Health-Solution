package lk.solution.health.excellent.resource.dao;

import lk.solution.health.excellent.resource.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository

public interface PatientDao extends JpaRepository<Patient, Integer> {
    Patient findFirstByOrderByIdDesc();

    Patient findByNic(String nic);

    Patient findByNumber(String number);

    Patient findByName(String name);
}
