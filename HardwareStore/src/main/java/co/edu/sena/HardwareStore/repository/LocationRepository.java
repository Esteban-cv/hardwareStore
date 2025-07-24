package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location,Integer> {
}
