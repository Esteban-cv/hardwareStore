package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Category;
import co.edu.sena.HardwareStore.model.Sale;
import co.edu.sena.HardwareStore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SaleController {
    @Autowired
    private SaleRepository saleRepository;
    private SaleDetailRepository saleDetailRepository;
    private ProductReturnsRepository productReturnsRepository;
    private ClientRepository clientRepository;
    private ArticleRepository articleRepository;

    @GetMapping("/sales")
    public String list(Model model) {
        model.addAttribute("sales", saleRepository.findAll());
        return "/sales/sale";
    }

    @GetMapping("/sale/form")
    public String form(Model model) {
        model.addAttribute("sale", new Sale());
        return "/sales/sale_form";
    }

    @PostMapping("/sale/save")
    public String save(@ModelAttribute Sale sale, RedirectAttributes ra) {
        saleRepository.save(sale);
        ra.addFlashAttribute("success", "Venta guardada exitosamente.");
        return "redirect:/sales";
    }

    @GetMapping("/sale/edit/{id}")
    public String edit(@PathVariable("id") Long idSale, Model model) {
        Sale sale = saleRepository.findById(idSale).orElse(null);
        model.addAttribute("sale", sale);
        return "sale_form";
    }

    @PostMapping("/sale/delete/{id}")
    public String delete(@PathVariable("id") Long idSale, RedirectAttributes ra) {
        saleRepository.deleteById(idSale);
        ra.addFlashAttribute("success", "Venta eliminada exitosamente.");
        return "redirect:/sales";
    }
}
