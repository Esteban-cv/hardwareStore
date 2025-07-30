package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Client;
import co.edu.sena.HardwareStore.model.Employee;
import co.edu.sena.HardwareStore.model.Sale;
import co.edu.sena.HardwareStore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private ProductReturnsRepository productReturnsRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public String listSales(Model model) {
        model.addAttribute("sales", saleRepository.findAll());
        return "sales/sale";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("sale", new Sale());
        model.addAttribute("clients", new Client());
        model.addAttribute("employees", new Employee());
        return "sales/sale_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Sale sale, RedirectAttributes ra) {
        saleRepository.save(sale);
        ra.addFlashAttribute("success", "Venta guardada exitosamente.");
        return "redirect:/sales";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long idSale, Model model, RedirectAttributes ra) {
        Sale sale = saleRepository.findById(idSale).orElse(null);
        if (sale == null) {
            ra.addFlashAttribute("error", "Venta no encontrada.");
            return "redirect:/sales";
        }
        model.addAttribute("sale", sale);
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        return "sales/sale_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idSale, RedirectAttributes ra) {
        saleRepository.deleteById(idSale);
        ra.addFlashAttribute("success", "Venta eliminada exitosamente.");
        return "redirect:/sales";
    }
}
