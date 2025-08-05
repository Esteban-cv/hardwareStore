package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.ProductReturns;
import co.edu.sena.HardwareStore.model.Sale;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.ProductReturnsRepository;
import co.edu.sena.HardwareStore.repository.SaleRepository;
import co.edu.sena.HardwareStore.services.PdfReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/productreturns")
public class ProductReturnController {
    @Autowired
    private ProductReturnsRepository productReturnsRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private PdfReportService pdfReportService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model){
        Page<ProductReturns> productReturns = productReturnsRepository.findAll(PageRequest.of(page, 10, Sort.by("date").descending()));
        model.addAttribute("productreturns", productReturns);
        return "sales/returns";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("productreturn", new ProductReturns()); //Para la fk
        model.addAttribute("sales", saleRepository.findAll()); //Para la fk
        model.addAttribute("articles", articleRepository.findAll()); //Para la fk
        return "sales/return_form";
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
        return "sales/return_form";

    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idProductReturns, RedirectAttributes ra){
        productReturnsRepository.deleteById(idProductReturns);
        ra.addFlashAttribute("success", "Producto retornado eliminado");
        return  "redirect:/productreturns";
    }

    @GetMapping("/returnreport")
    public void generatereturnReport(HttpServletResponse response) throws IOException {
        try {
            List<ProductReturns> productReturns = productReturnsRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Cantidad", "Fecha", "Cantidad Venta", "Articulo");
            List<List<String>> rows = productReturns.stream()
                    .map(r -> {
                        String fechaFormateada = r.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(r.getIdReturn()),
                                String.valueOf(r.getQuantity()),
                                fechaFormateada,
                                String.valueOf(r.getSale() != null ? r.getSale().getTotal() : "N/A"),
                                r.getArticle() != null ? r.getArticle().getName() : "N/A"
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_devoluciones.pdf");
            pdfReportService.generatePdf(response, "Reporte de devoluciones", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }
}
