package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Inventory;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.InventoryRepository;
import co.edu.sena.HardwareStore.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventories")
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public String list(Model model){
        model.addAttribute("inventories", inventoryRepository.findAll());
        return "/inventories/inventory";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("inventory", new Inventory());
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "/inventories/inventory_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Inventory inventory, RedirectAttributes ra){
        inventoryRepository.save(inventory);
        ra.addFlashAttribute("success", "Inventario guardado exitosamente.");
        return "redirect:/inventories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idInventory, Model model, RedirectAttributes ra){
        Inventory inventory = inventoryRepository.findById(idInventory).orElse(null);
        if (inventory == null){
            ra.addFlashAttribute("error", "Inventario no encontrado.");
            return "redirect:/inventories";
        }
        model.addAttribute("inventory", inventory);
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "/inventories/inventory_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idInventory, RedirectAttributes ra){
        inventoryRepository.deleteById(idInventory);
        ra.addFlashAttribute("success", "Inventario eliminado exitosamente");
        return "redirect:/inventories";
    }
}
