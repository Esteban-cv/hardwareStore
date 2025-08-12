package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale,Long> {
	@Query("SELECT s FROM Sale s LEFT JOIN FETCH s.details d LEFT JOIN FETCH d.article WHERE s.idSale = :id")
	Optional<Sale> findByIdWithDetails(@Param("id") Long id);
}
