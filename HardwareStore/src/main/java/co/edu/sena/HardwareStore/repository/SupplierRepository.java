package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {
}
