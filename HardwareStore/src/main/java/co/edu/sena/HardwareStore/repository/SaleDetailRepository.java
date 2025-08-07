package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail,Integer> {
    List<SaleDetail> findBySale_IdSale(Long idSale);
}
