package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,Long> {
}
