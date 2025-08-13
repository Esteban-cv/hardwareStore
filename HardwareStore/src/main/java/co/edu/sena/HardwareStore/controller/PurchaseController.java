package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Purchase;
import co.edu.sena.HardwareStore.model.Supplier;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.EmployeeRepository;
import co.edu.sena.HardwareStore.repository.PurchaseRepository;
import co.edu.sena.HardwareStore.repository.SupplierRepository;
import co.edu.sena.HardwareStore.services.ExcelReportService;
import co.edu.sena.HardwareStore.services.PdfReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private PdfReportService pdfReportService;
    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping
    public String listPurchases(Model model) {
        List<Purchase> purchases = purchaseRepository.findAll(Sort.by("date").descending());
        purchases.forEach(s -> {
            if (s.getTotal() != null) {
                s.setTotal(s.getTotal().setScale(2, RoundingMode.HALF_UP));
            }
        });
        model.addAttribute("purchases", purchases);
        return "purchases/purchase";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("articles", articleRepository.findAll()); // Para la FK
        model.addAttribute("suppliers", supplierRepository.findAll()); // Para la FK
        model.addAttribute("employees", employeeRepository.findAll()); // Para laFK
        return "/purchases/purchase_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Purchase purchase, RedirectAttributes ra) {
        try {
            boolean esNuevo = (purchase.getIdPurchase() == null);

            purchaseRepository.save(purchase);

            if (esNuevo) {
                ra.addFlashAttribute("success", "Compra creada exitosamente");
            } else {
                ra.addFlashAttribute("success", "Compra actualizada exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la compra");
        }
        return "redirect:/purchases";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long idPurchase, Model model, RedirectAttributes ra) {
        try {
            Purchase purchase = purchaseRepository.findById(idPurchase)
                    .orElseThrow(() -> new Exception("Compra no encontrada"));
            model.addAttribute("purchase", purchase);
            model.addAttribute("articles", articleRepository.findAll());
            model.addAttribute("suppliers", supplierRepository.findAll());
            model.addAttribute("employees", employeeRepository.findAll());
            return "purchases/purchase_form";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Compra no encontrada");
            return "redirect:/purchases";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idPurchase, RedirectAttributes ra) {
        purchaseRepository.deleteById(idPurchase);
        ra.addFlashAttribute("success", "Compra eliminada exitosamente.");
        return "redirect:/purchases";
    }

    @GetMapping("/purchasereport")
    public void generatepurchaseReport(HttpServletResponse response) throws IOException {
        try {
            List<Purchase> purchases = purchaseRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha", "Cantidad", "Total", "Estado", "Proveedor", "Empleado",
                    "Articulo");
            List<List<String>> rows = purchases.stream()
                    .map(s -> {
                        // Formatear fecha (ejemplo para java.util.Date)
                        String fechaFormateada = s.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdPurchase()),
                                fechaFormateada,
                                String.valueOf(s.getQuantity()),
                                String.valueOf(s.getTotal()),
                                String.valueOf(s.getStatus()),
                                s.getSupplier() != null ? s.getSupplier().getName() : "N/A",
                                s.getEmployee() != null ? s.getEmployee().getName() : "N/A",
                                s.getArticle() != null ? s.getArticle().getName() : "N/A");
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_compras.pdf");
            pdfReportService.generatePdf(response, "Reporte de Compras", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @GetMapping("/purchasereport/excel")
    public void generatePurchaseExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Purchase> purchases = purchaseRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha", "Cantidad", "Total", "Estado", "Proveedor", "Empleado",
                    "Articulo");
            List<List<String>> rows = purchases.stream()
                    .map(s -> {
                        String fechaFormateada = s.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdPurchase()),
                                fechaFormateada,
                                String.valueOf(s.getQuantity()),
                                String.valueOf(s.getTotal()),
                                String.valueOf(s.getStatus()),
                                s.getSupplier() != null ? s.getSupplier().getName() : "N/A",
                                s.getEmployee() != null ? s.getEmployee().getName() : "N/A",
                                s.getArticle() != null ? s.getArticle().getName() : "N/A");
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_compras.xlsx");

            excelReportService.generateExcel(response, "Compras", headers, rows);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}
