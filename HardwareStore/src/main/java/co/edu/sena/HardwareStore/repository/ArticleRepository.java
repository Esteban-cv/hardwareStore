package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    @Query("SELECT COUNT(a) FROM Article a WHERE a.supplier.idSupplier = :supplierId")
    long countBySupplierIdSupplier(@Param("supplierId") Long supplierId);

    // O si prefieres obtener la lista completa:
    @Query("SELECT a FROM Article a WHERE a.supplier.idSupplier = :supplierId")
    List<Article> findBySupplierIdSupplier(@Param("supplierId") Long supplierId);

    @Query("SELECT COUNT(a) FROM Article a")
    long countArticles();

}
