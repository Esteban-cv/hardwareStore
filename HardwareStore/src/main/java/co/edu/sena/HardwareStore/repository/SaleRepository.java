package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {
	@Query("""
			    SELECT DISTINCT s
			    FROM Sale s
			    LEFT JOIN FETCH s.employee e
			    LEFT JOIN FETCH s.details d
			    LEFT JOIN FETCH d.article a
			    WHERE s.idSale = :id
			""")
	Optional<Sale> findByIdWithDetails(@Param("id") Long id);

	// Clientes distintos con ventas desde fecha
    @Query("SELECT COUNT(DISTINCT s.client.idClient) FROM Sale s WHERE s.date >= :from")
    long countActiveClientsSince(@Param("from") LocalDate fromDate);

    @Query("SELECT COUNT(s) FROM Sale s")
	long countSales();


}
