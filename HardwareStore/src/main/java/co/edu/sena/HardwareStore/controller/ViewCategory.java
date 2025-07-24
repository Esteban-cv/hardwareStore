package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.Category;
import co.edu.sena.HardwareStore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViewCategory {
    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("/categories")
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return  "category";
    }

    @GetMapping("/category/form")
    public String form(Model model) {
        model.addAttribute("category", new Category());
        return "category_form";
    }

    @PostMapping("/category/save")
    public String save(@ModelAttribute Category category, RedirectAttributes ra) {
        categoryRepository.save(category);
        ra.addFlashAttribute("success", "Categoria guardada exitosamente.");
        return "redirect:/categories";
    }

    @GetMapping("/category/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Category category = categoryRepository.findById(id).orElse(null);
        model.addAttribute("category", category);
        return "category_form";
    }

    @PostMapping("/category/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        categoryRepository.deleteById(id);
        ra.addFlashAttribute("success", "Categoria elimianada exitosamente.");
        return "redirect:/categories";
    }
}
