package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue,Long> {
}
