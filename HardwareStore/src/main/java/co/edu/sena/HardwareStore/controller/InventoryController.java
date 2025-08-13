package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Article;
import co.edu.sena.HardwareStore.model.Inventory;
import co.edu.sena.HardwareStore.model.Location;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.InventoryRepository;
import co.edu.sena.HardwareStore.repository.LocationRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller // Indica que esta clase es un controlador de Spring MVC
@RequestMapping("/inventory") // Ruta base para todas las operaciones de inventario
public class InventoryController {

    // Inyección de dependencias para acceder a la base de datos y servicios
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ExcelReportService excelReportService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PdfReportService pdfReportService;

    // Listar inventario ordenado por fecha de actualización descendente
    @GetMapping
    public String listInventory(Model model) {
        List<Inventory> inventories = inventoryRepository.findAll(Sort.by("updatedAt").descending());
        model.addAttribute("inventories", inventories);
        return "inventory/all_inventory"; // Vista con todos los registros
    }

    // Formulario para crear o editar inventario
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("inventory", new Inventory()); // Objeto vacío para el formulario
        model.addAttribute("articles", articleRepository.findAll()); // Lista de artículos disponibles
        model.addAttribute("locations", locationRepository.findAll()); // Lista de ubicaciones
        return "inventory/inventory_form";
    }

    // Guardar inventario (crear o actualizar)
    @PostMapping("/save")
    public String save(@ModelAttribute Inventory inventory, RedirectAttributes ra) {

        // Validar que se haya seleccionado un artículo
        if (inventory.getArticle() == null || inventory.getArticle().getIdArticle() == null) {
            ra.addFlashAttribute("error", "Debe seleccionar un artículo");
            return "redirect:/inventory/form";
        }

        // Validar que se haya seleccionado una ubicación
        if (inventory.getLocation() == null || inventory.getLocation().getIdLocation() == null) {
            ra.addFlashAttribute("error", "Debe seleccionar una ubicación");
            return "redirect:/inventory/form";
        }

        // Cargar artículo y ubicación desde la base de datos
        Article article = articleRepository.findById(inventory.getArticle().getIdArticle()).orElse(null);
        Location location = locationRepository.findById(inventory.getLocation().getIdLocation()).orElse(null);

        // Validar existencia en BD
        if (article == null) {
            ra.addFlashAttribute("error", "Artículo no encontrado");
            return "redirect:/inventory/form";
        }
        if (location == null) {
            ra.addFlashAttribute("error", "Ubicación no encontrada");
            return "redirect:/inventory/form";
        }

        // Asignar entidades gestionadas por JPA
        inventory.setArticle(article);
        inventory.setLocation(location);

        // Si es nuevo, establecer la fecha de actualización actual
        if (inventory.getIdInventory() == null) {
            inventory.setUpdatingDate(LocalDate.now());
        }

        // Guardar en la base de datos
        inventoryRepository.save(inventory);
        ra.addFlashAttribute("success", "Inventario guardado exitosamente.");
        return "redirect:/inventory";
    }

    // Cargar datos en el formulario para edición
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idInventory, Model model, RedirectAttributes ra) {
        Inventory inventory = inventoryRepository.findById(idInventory).orElse(null);
        if (inventory == null) {
            ra.addFlashAttribute("error", "Inventario no encontrado.");
            return "redirect:/inventory";
        }
        model.addAttribute("inventory", inventory);
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "inventory/inventory_form";
    }

    // Eliminar inventario
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idInventory, RedirectAttributes ra) {
        inventoryRepository.deleteById(idInventory);
        ra.addFlashAttribute("success", "Inventario eliminado exitosamente");
        return "redirect:/inventory";
    }

    // Generar reporte en PDF
    @GetMapping("/inventoryreport")
    public void generateInventoryReport(HttpServletResponse response) throws IOException {
        try {
            // Obtener todos los registros de inventario
            List<Inventory> all_inventory = inventoryRepository.findAll();

            // Encabezados del reporte
            List<String> headers = Arrays.asList("ID", "Stock Actual", "Stock Mínimo", "Fecha Actualización",
                    "Articulo", "Ubicación");

            // Filas del reporte
            List<List<String>> rows = all_inventory.stream()
                    .map(s -> {
                        // Formatear fecha a dd/MM/yyyy
                        String fechaFormateada = s.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdInventory()),
                                String.valueOf(s.getCurrentStock()),
                                String.valueOf(s.getMinimumStock()),
                                fechaFormateada,
                                s.getArticle() != null ? s.getArticle().getName() : "N/A",
                                s.getLocation() != null ? s.getLocation().getName() : "N/A");
                    })
                    .collect(Collectors.toList());

            // Configurar respuesta HTTP como PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_inventario.pdf");

            // Generar PDF usando el servicio
            pdfReportService.generatePdf(response, "Reporte de Inventario", headers, rows);

        } catch (Exception e) {
            // Manejo de errores
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    // Generar reporte en Excel
    @GetMapping("/inventoryreport/excel")
    public void generateInventoryExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Inventory> all_inventory = inventoryRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Stock Actual", "Stock Mínimo", "Fecha Actualización",
                    "Articulo", "Ubicación");

            List<List<String>> rows = all_inventory.stream()
                    .map(s -> {
                        String fechaFormateada = s.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdInventory()),
                                String.valueOf(s.getCurrentStock()),
                                String.valueOf(s.getMinimumStock()),
                                fechaFormateada,
                                s.getArticle() != null ? s.getArticle().getName() : "N/A",
                                s.getLocation() != null ? s.getLocation().getName() : "N/A");
                    })
                    .collect(Collectors.toList());

            // Configurar respuesta HTTP como archivo Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_inventario.xlsx");

            // Generar Excel usando el servicio
            excelReportService.generateExcel(response, "Inventario", headers, rows);

        } catch (Exception e) {
            // Manejo de errores
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}
