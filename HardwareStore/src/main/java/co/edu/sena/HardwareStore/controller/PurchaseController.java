package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.Purchase;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.EmployeeRepository;
import co.edu.sena.HardwareStore.repository.PurchaseRepository;
import co.edu.sena.HardwareStore.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @GetMapping
    public String list(Model model) {
        model.addAttribute("purchases", purchaseRepository.findAll());
        return "/purchases/purchase";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("articles", articleRepository.findAll());   //  Para la FK
        model.addAttribute("suppliers", supplierRepository.findAll()); // Para la FK
        model.addAttribute("employees", employeeRepository.findAll()); // Para laFK
        return "/purchases/purchase_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Purchase purchase, RedirectAttributes ra) {
        purchaseRepository.save(purchase);
        ra.addFlashAttribute("success", "Compra guardada exitosamente.");
        return "redirect:/purchases";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long idPurchase, Model model, RedirectAttributes ra) {
        Purchase purchase = purchaseRepository.findById(idPurchase).orElse(null);
        if (purchase == null) {
            ra.addFlashAttribute("error", "Compra no encontrada.");
            return "redirect:/purchases";
        }
        model.addAttribute("purchase", purchase);
        model.addAttribute("articles", articleRepository.findAll());   // FK
        model.addAttribute("suppliers", supplierRepository.findAll()); // FK
        model.addAttribute("employees", employeeRepository.findAll()); // FK
        return "/purchases/purchase_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idPurchase, RedirectAttributes ra) {
        purchaseRepository.deleteById(idPurchase);
        ra.addFlashAttribute("success", "Compra eliminada exitosamente.");
        return "redirect:/purchases";
    }
}

