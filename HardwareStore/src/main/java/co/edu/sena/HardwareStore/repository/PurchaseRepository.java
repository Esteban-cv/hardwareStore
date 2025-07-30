package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase,Long> {
}
