package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<Entry,Integer> {
}
