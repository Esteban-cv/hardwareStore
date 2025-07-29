package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.Article;
import co.edu.sena.HardwareStore.model.Category;
import co.edu.sena.HardwareStore.model.Unit;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.CategoryRepository;
import co.edu.sena.HardwareStore.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UnitRepository unitRepository;

    @GetMapping("/articles")
    public String listArticles(Model model){
        model.addAttribute("articles", articleRepository.findAll());
        return "catalog/articles";
    }

    @GetMapping("/categories")
    public String listCategories(Model model){
        model.addAttribute("categories", categoryRepository.findAll());
        return "catalog/categories";
    }

    @GetMapping("/form/article")
    public String formArticle(Model model){
        model.addAttribute("article", new Article());
        model.addAttribute("category", new Category());
        model.addAttribute("unit", new Unit());
        return "catalog/article_form";
    }

    @GetMapping("/form/category")
    public String formCategory(Model model){
        model.addAttribute("category", new Category());
        return "catalog/category_form";
    }

    @PostMapping("/save/article")
    public String saveArticle(@ModelAttribute Article article, RedirectAttributes ra){
        articleRepository.save(article);
        ra.addFlashAttribute("success", "Artículo guardado exitosamente");
        return "redirect:/articles";
    }

    @PostMapping("/save/category")
    public String saveCategory(@ModelAttribute Category category, RedirectAttributes ra){
        categoryRepository.save(category);
        ra.addFlashAttribute("success", "Categoria guardada exitosamente");
        return "redirect:/categories";
    }

    @GetMapping("/editArticle/{id}")
    public String editArticle(@PathVariable("id") Integer idArticle, Model model, RedirectAttributes ra){
        Article article = articleRepository.findById(idArticle).orElse(null);
        if (article == null){
            ra.addFlashAttribute("error", "Artículo no encontrado");
            return "redirect:/articles";
        }
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "catalog/article_form";
    }

    @GetMapping("/editCategory/{id}")
    public String edit(@PathVariable Integer idCategory, Model model, RedirectAttributes ra){
        Category category = categoryRepository.findById(idCategory).orElse(null);
        if(category == null){
            ra.addFlashAttribute("error", "Categoria no encontrada");
            return "redirect:/categories";
        }
        model.addAttribute("category", category);
        return "catalog/category_form";
    }

    @PostMapping("/deleteArticle/{id}")
    public String deleteArticle(@PathVariable("id") Integer idArticle, RedirectAttributes ra){
        articleRepository.deleteById(idArticle);
        ra.addFlashAttribute("success", "Artículo eliminado exitosamente");
        return "redirect:/articles";
    }

    @PostMapping("/deleteCategories/{id}")
    public String deleteCategories(@PathVariable("id") Integer idCategory, RedirectAttributes ra){
        categoryRepository.deleteById(idCategory);
        ra.addFlashAttribute("success", "Categoria eliminada exitosamente");
        return "redirect:/categories";
    }
}
