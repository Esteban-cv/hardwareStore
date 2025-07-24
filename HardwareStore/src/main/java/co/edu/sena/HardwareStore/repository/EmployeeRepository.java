package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
}
