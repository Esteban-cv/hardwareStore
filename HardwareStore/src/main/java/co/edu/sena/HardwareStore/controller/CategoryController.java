package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.Category;
import co.edu.sena.HardwareStore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model){
        model.addAttribute("categories", categoryRepository.findAll());
        return "catalog/categories";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("category", new Category());
        return "catalog/category_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Category category, RedirectAttributes ra){
        categoryRepository.save(category);
        ra.addFlashAttribute("success", "Categoria guardada exitosamente");
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer idCategory, Model model, RedirectAttributes ra){
        Category category = categoryRepository.findById(idCategory).orElse(null);
        if(category == null){
            ra.addFlashAttribute("error", "Categoria no encontrada");
            return "redirect:/categories";
        }
        model.addAttribute("category", category);
        return "/catalog/category_form";
    }

    @PostMapping ("/delete/{id}")
    public String delete(@PathVariable("id") Integer idCategory, RedirectAttributes ra){
        categoryRepository.deleteById(idCategory);
        ra.addFlashAttribute("success", "Categoria eliminada exitosamente");
        return "redirect:/categories";
    }
}
