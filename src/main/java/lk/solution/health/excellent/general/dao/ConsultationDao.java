package lk.solution.health.excellent.general.dao;

import lk.solution.health.excellent.general.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationDao extends JpaRepository<Consultation, Integer> {
}
