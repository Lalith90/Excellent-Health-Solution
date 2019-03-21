package lk.solution.health.excellent.resource.dao;

import lk.solution.health.excellent.resource.entity.Enum.MedicalPackageStatus;
import lk.solution.health.excellent.resource.entity.MedicalPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository

public interface MedicalPackageDao extends JpaRepository<MedicalPackage, Integer> {

    List<MedicalPackage> findByMedicalPackageStatus(MedicalPackageStatus val);
}
