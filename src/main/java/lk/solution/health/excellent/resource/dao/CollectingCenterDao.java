package lk.solution.health.excellent.resource.dao;

import lk.solution.health.excellent.resource.entity.CollectingCenter;
import lk.solution.health.excellent.resource.entity.Enum.CollectingCenterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CollectingCenterDao extends JpaRepository<CollectingCenter, Integer> {

    List<CollectingCenter> findByCollectingCenterStatus(CollectingCenterStatus val);

    CollectingCenter findFirstByOrderByIdAsc();


}
