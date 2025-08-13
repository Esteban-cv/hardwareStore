package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long> {

    long count();

    // Clientes creados desde una fecha (para crecimiento por mes)
    @Query(value = """
                SELECT DATE_FORMAT(c.created_at, '%Y-%m') AS ym, COUNT(*) AS qty
                FROM client c
                WHERE c.created_at >= :from
                GROUP BY ym
                ORDER BY ym
            """, nativeQuery = true)
    List<Object[]> countByMonthSince(@Param("from") LocalDateTime from);

    // Top N clientes recientes (para la tabla)
    @Query("SELECT c FROM Client c ORDER BY c.createdAt DESC")
    Page<Client> findRecent(Pageable pageable);

    // CONSULTA NATIVA SQL - usando nombres reales de la base de datos
    // Los 10 clientes más recientes (independiente de si compraron)
    @Query(value = """
                SELECT c.id_client, c.document, c.name, c.phone, c.email, c.rut,
                       c.created_at, c.updated_at, c.address,
                       COALESCE(SUM(s.total), 0) as total_spent
                FROM client c
                LEFT JOIN sale s ON s.id_client = c.id_client
                GROUP BY c.id_client, c.document, c.name, c.phone, c.email, c.rut, c.created_at, c.updated_at, c.address
                ORDER BY c.created_at DESC
                LIMIT 10
            """, nativeQuery = true)
    List<Object[]> findRecentClientsWithTotalSpent();

    // En ClientRepository - Los 10 clientes que MÁS han gastado
    @Query(value = """
                SELECT c.id_client, c.document, c.name, c.phone, c.email, c.rut,
                       c.created_at, c.updated_at, c.address,
                       COALESCE(SUM(s.total), 0) as total_spent
                FROM client c
                LEFT JOIN sale s ON s.id_client = c.id_client
                GROUP BY c.id_client, c.document, c.name, c.phone, c.email, c.rut, c.created_at, c.updated_at, c.address
                ORDER BY total_spent DESC, c.created_at DESC
                LIMIT 10
            """, nativeQuery = true)
    List<Object[]> findTopSpendingClients();

    @Query("SELECT COUNT(c) FROM Client c")
    long countClients();

}
