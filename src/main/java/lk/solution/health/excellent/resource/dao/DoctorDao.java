package lk.solution.health.excellent.resource.dao;

import lk.solution.health.excellent.resource.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository

public interface DoctorDao extends JpaRepository<Doctor, Integer> {
}
