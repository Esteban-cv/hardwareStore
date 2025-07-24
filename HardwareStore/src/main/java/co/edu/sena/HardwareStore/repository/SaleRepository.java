package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale,Long> {
}
