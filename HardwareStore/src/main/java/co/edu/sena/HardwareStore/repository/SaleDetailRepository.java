package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail,Integer> {
    List<SaleDetail> findBySale_IdSale(Long idSale);

    @Query("SELECT sd FROM SaleDetail sd WHERE sd.sale.idSale = :saleId")
    List<SaleDetail> findBySaleId(@Param("saleId") Long saleId);
    
    @Query("SELECT sd FROM SaleDetail sd JOIN FETCH sd.article WHERE sd.sale.idSale = :saleId")
    List<SaleDetail> findBySaleIdWithArticles(@Param("saleId") Long saleId);
}
