package lk.solution.health.excellent.transaction.dao;

import lk.solution.health.excellent.transaction.entity.DiscountRatio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface DiscountRatioDao extends JpaRepository<DiscountRatio, Integer> {

}
