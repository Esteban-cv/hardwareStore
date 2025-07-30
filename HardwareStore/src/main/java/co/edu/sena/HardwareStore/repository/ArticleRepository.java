package co.edu.sena.HardwareStore.repository;

import co.edu.sena.HardwareStore.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article,Integer> {
}
