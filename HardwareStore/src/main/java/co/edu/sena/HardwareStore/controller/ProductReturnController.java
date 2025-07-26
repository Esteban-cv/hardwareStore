package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.ProductReturns;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.ProductReturnsRepository;
import co.edu.sena.HardwareStore.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productreturns")
public class ProductReturnController {
    @Autowired
    private ProductReturnsRepository productReturnsRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public String list(Model model){
        model.addAttribute("productreturns", productReturnsRepository.findAll());
        return "/productreturn/productreturns";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("productreturn", new ProductReturns()); //Para la fk
        model.addAttribute("sales", saleRepository.findAll()); //Para la fk
        model.addAttribute("articles", articleRepository.findAll()); //Para la fk
        return "/productreturns/productreturn_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ProductReturns productReturns, RedirectAttributes ra){
        productReturnsRepository.save(productReturns);
        ra.addFlashAttribute("success", "Producto guardado exitosamente");
        return "redirect:/productreturns";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idProductReturns, Model model, RedirectAttributes ra){
        ProductReturns productReturns = productReturnsRepository.findById(idProductReturns).orElse(null);
        if(productReturns == null){
            ra.addFlashAttribute("error", "No se encontroó la devolución solicitada");
            return "redirect:/productreturns";
        }
        model.addAttribute("productreturn", productReturns);
        model.addAttribute("sales", saleRepository.findAll()); //Para la fk
        model.addAttribute("articles", articleRepository.findAll()); //Para la fk
        return "/productreturns/productreturn_form";

    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idProductReturns, RedirectAttributes ra){
        productReturnsRepository.deleteById(idProductReturns);
        ra.addFlashAttribute("success", "Producto retornado eliminado");
        return  "redirect:/productreturns";
    }
}
