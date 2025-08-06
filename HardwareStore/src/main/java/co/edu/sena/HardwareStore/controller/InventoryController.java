package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Article;
import co.edu.sena.HardwareStore.model.Inventory;
import co.edu.sena.HardwareStore.model.Location;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.InventoryRepository;
import co.edu.sena.HardwareStore.repository.LocationRepository;
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
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PdfReportService pdfReportService;

    @GetMapping
    public String listInventory(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Inventory> inventories = inventoryRepository.findAll(PageRequest.of(page, 10, Sort.by("updatedAt").descending()));
        model.addAttribute("inventories", inventories);
        return "inventory/all_inventory";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("inventory", new Inventory());
        model.addAttribute("article", new Article());
        model.addAttribute("locations", new Location());
        return "inventory/inventory_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Inventory inventory, RedirectAttributes ra){
        inventoryRepository.save(inventory);
        ra.addFlashAttribute("success", "Inventario guardado exitosamente.");
        return "redirect:/inventory";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idInventory, Model model, RedirectAttributes ra){
        Inventory inventory = inventoryRepository.findById(idInventory).orElse(null);
        if (inventory == null){
            ra.addFlashAttribute("error", "Inventario no encontrado.");
            return "redirect:/inventory";
        }
        model.addAttribute("inventory", inventory);
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "inventory/inventory_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idInventory, RedirectAttributes ra){
        inventoryRepository.deleteById(idInventory);
        ra.addFlashAttribute("success", "Inventario eliminado exitosamente");
        return "redirect:/inventory";
    }

    @GetMapping("/inventoryreport")
    public void generateInventoryReport(HttpServletResponse response) throws IOException {
        try {
            List<Inventory> all_inventory = inventoryRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Stock Actual", "Stock Mínimo", "Fecha Actualización", "Articulo", "Ubicación");
            List<List<String>> rows = all_inventory .stream()
                    .map(s -> {
                        // Formatear fecha (ejemplo para java.util.Date)
                        String fechaFormateada = s.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdInventory()),
                                String.valueOf(s.getCurrentStock()),
                                String.valueOf(s.getMinimumStock()),
                                fechaFormateada, // ✅ Aquí va la fecha correctamente
                                s.getArticle() != null ? s.getArticle().getName() : "N/A",
                                s.getLocation() != null ? s.getLocation().getName() : "N/A"
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_inventario.pdf");
            pdfReportService.generatePdf(response, "Reporte de Inventario", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }
}
