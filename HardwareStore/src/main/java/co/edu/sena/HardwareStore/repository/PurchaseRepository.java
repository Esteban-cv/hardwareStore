package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseRepository extends JpaRepository<Purchase,Long> {
    @Query("SELECT COUNT(p) FROM Purchase p")
    long countPurchases();
}
