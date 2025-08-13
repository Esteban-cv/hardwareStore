package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Purchase;
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

/**
 * Controlador para gestionar las operaciones relacionadas con las compras.
 * Incluye métodos para listar, crear, editar, eliminar y generar reportes
 * en formato PDF y Excel.
 */
@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    /** Repositorio para la gestión de compras */
    @Autowired
    private PurchaseRepository purchaseRepository;

    /** Repositorio para la gestión de artículos */
    @Autowired
    private ArticleRepository articleRepository;

    /** Repositorio para la gestión de proveedores */
    @Autowired
    private SupplierRepository supplierRepository;

    /** Repositorio para la gestión de empleados */
    @Autowired
    private EmployeeRepository employeeRepository;

    /** Servicio para la generación de reportes PDF */
    @Autowired
    private PdfReportService pdfReportService;

    /** Servicio para la generación de reportes Excel */
    @Autowired
    private ExcelReportService excelReportService;

    /**
     * Lista todas las compras ordenadas por fecha descendente.
     *
     * @param model Objeto para enviar datos a la vista.
     * @return Nombre de la plantilla de la lista de compras.
     */
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

    /**
     * Muestra el formulario para registrar una nueva compra.
     *
     * @param model Objeto para enviar datos a la vista.
     * @return Nombre de la plantilla del formulario de compra.
     */
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        return "/purchases/purchase_form";
    }

    /**
     * Guarda una compra nueva o actualizada.
     *
     * @param purchase Objeto Purchase a guardar.
     * @param ra Objeto para enviar mensajes flash.
     * @return Redirección a la lista de compras.
     */
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

    /**
     * Muestra el formulario de edición de una compra existente.
     *
     * @param idPurchase ID de la compra a editar.
     * @param model Objeto para enviar datos a la vista.
     * @param ra Objeto para enviar mensajes flash.
     * @return Nombre de la plantilla del formulario o redirección si no se encuentra.
     */
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

    /**
     * Elimina una compra por su ID.
     *
     * @param idPurchase ID de la compra a eliminar.
     * @param ra Objeto para enviar mensajes flash.
     * @return Redirección a la lista de compras.
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idPurchase, RedirectAttributes ra) {
        purchaseRepository.deleteById(idPurchase);
        ra.addFlashAttribute("success", "Compra eliminada exitosamente.");
        return "redirect:/purchases";
    }

    /**
     * Genera un reporte PDF de todas las compras registradas.
     *
     * @param response Objeto HttpServletResponse para enviar el archivo.
     * @throws IOException En caso de error al escribir la respuesta.
     */
    @GetMapping("/purchasereport")
    public void generatepurchaseReport(HttpServletResponse response) throws IOException {
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

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_compras.pdf");
            pdfReportService.generatePdf(response, "Reporte de Compras", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    /**
     * Genera un reporte Excel de todas las compras registradas.
     *
     * @param response Objeto HttpServletResponse para enviar el archivo.
     * @throws IOException En caso de error al escribir la respuesta.
     */
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
