package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.Article;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.CategoryRepository;
import co.edu.sena.HardwareStore.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UnitRepository unitRepository;

    @GetMapping
    public String list(Model model){
        model.addAttribute("articles", articleRepository.findAll());
        return "/articles/article";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "/articles/article_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Article article, RedirectAttributes ra){
        articleRepository.save(article);
        ra.addFlashAttribute("success", "Artículo guardado exitosamente");
        return "redirect:/articles";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idArticle, Model model, RedirectAttributes ra){
        Article article = articleRepository.findById(idArticle).orElse(null);
        if (article == null){
            ra.addFlashAttribute("error", "Artículo no encontrado");
            return "redirect:/articles";
        }

        model.addAttribute("article", article);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "/articles/article_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idArticle, RedirectAttributes ra){
        articleRepository.deleteById(idArticle);
        ra.addFlashAttribute("success", "Artículo eliminado exitosamente");
        return "redirect:/articles";
    }
}
