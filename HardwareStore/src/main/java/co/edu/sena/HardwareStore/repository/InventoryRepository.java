package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory,Integer> {
}
