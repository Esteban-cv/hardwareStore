package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.Entry;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/entries")
public class EntryController {
    @Autowired
    private EntryRepository entryRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public String list(Model model){
        model.addAttribute("entries", entryRepository.findAll());
        return "inventory/entries";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("entry", new Entry());
        model.addAttribute("articles", articleRepository.findAll());
        return "inventory/entry_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Entry entry, RedirectAttributes ra){
        entryRepository.save(entry);
        ra.addFlashAttribute("success", "Entrada guardada exitosamente");
        return "redirect:/entries";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idEntry, Model model, RedirectAttributes ra){
        Entry entry = entryRepository.findById(idEntry).orElse(null);
        if(entry == null){
            ra.addFlashAttribute("error", "Entrada no encontrada");
            return "redirect:/entries";
        }

        model.addAttribute("entry", entry);
        model.addAttribute("articles", articleRepository.findAll());
        return "inventory/entry_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idEntry, RedirectAttributes ra){
        entryRepository.deleteById(idEntry);
        ra.addFlashAttribute("success", "Entrada eliminada exitosamente");
        return "redirect:/entries";
    }

}
