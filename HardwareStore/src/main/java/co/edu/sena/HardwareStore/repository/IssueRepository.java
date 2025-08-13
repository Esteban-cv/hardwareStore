package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IssueRepository extends JpaRepository<Issue,Long> {
    @Query("SELECT COUNT(i) FROM Issue i")
    long countIssues();

}
