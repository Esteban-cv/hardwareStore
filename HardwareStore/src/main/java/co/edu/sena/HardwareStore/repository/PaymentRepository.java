package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {
}
