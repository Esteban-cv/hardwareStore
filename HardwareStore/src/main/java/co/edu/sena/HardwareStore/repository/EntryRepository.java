package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EntryRepository extends JpaRepository<Entry, Integer> {
    @Query("SELECT COUNT(e) FROM Entry e")
    long countEntries();
}
