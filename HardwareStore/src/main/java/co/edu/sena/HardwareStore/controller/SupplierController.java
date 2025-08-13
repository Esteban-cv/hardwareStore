package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Supplier;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private PdfReportService pdfReportService;
    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping
    public String listSuppliers(Model model) {
        List<Supplier> suppliers = supplierRepository.findAll(Sort.by("idSupplier").descending());
        model.addAttribute("suppliers", suppliers);
        return "suppliers/supplier";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "suppliers/supplier_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Supplier supplier, RedirectAttributes ra) {
        try {
            boolean esNuevo = (supplier.getIdSupplier() == null);

            supplierRepository.save(supplier);

            if (esNuevo) {
                ra.addFlashAttribute("success", "Proveedor creado exitosamente");
            } else {
                ra.addFlashAttribute("success", "Proveedor actualizado exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el proveedor");
        }
        return "redirect:/suppliers";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long idSupplier, Model model, RedirectAttributes ra) {
        try {
            Supplier supplier = supplierRepository.findById(idSupplier)
                    .orElseThrow(() -> new Exception("Proveedor no encontrado"));
            model.addAttribute("supplier", supplier);
            return "suppliers/supplier_form";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Proveedor no encontrado");
            return "redirect:/suppliers";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idSupplier, RedirectAttributes ra) {
        try {
            // Obtener información del proveedor
            Supplier supplier = supplierRepository.findById(idSupplier)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

            // Contar artículos asociados (más eficiente)
            long articleCount = articleRepository.countBySupplierIdSupplier(idSupplier);

            if (articleCount > 0) {
                ra.addFlashAttribute("deleteError", true);
                ra.addFlashAttribute("deleteErrorMessage",
                        String.format(
                                "No se puede eliminar el proveedor \"%s\" porque tiene %d artículo(s) activo(s). " +
                                        "Elimine primero los artículos o asígnelos a otro proveedor.",
                                supplier.getName(), articleCount));
                return "redirect:/suppliers";
            }

            // Eliminar si no tiene artículos asociados
            String supplierName = supplier.getName();
            supplierRepository.deleteById(idSupplier);

            ra.addFlashAttribute("success", "Proveedor \"" + supplierName + "\" eliminado exitosamente");
            ra.addFlashAttribute("successType", "DELETE_SUCCESS");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error inesperado al eliminar el proveedor: " + e.getMessage());
            ra.addFlashAttribute("errorType", "DELETE_ERROR");
        }

        return "redirect:/suppliers";
    }

    @GetMapping("/supplierreport")
    public void generateSupplierReport(HttpServletResponse response) throws IOException {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre", "Documento", "Teléfono");
            List<List<String>> rows = suppliers.stream()
                    .map(s -> Arrays.asList(
                            String.valueOf(s.getIdSupplier()),
                            s.getName(),
                            s.getDocument(),
                            s.getPhone()))
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_proveedores.pdf");
            pdfReportService.generatePdf(response, "Reporte de Proveedores", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @GetMapping("/supplierreport/excel")
    public void generateSupplierExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre", "Documento", "Teléfono");
            List<List<String>> rows = suppliers.stream()
                    .map(s -> Arrays.asList(
                            String.valueOf(s.getIdSupplier()),
                            s.getName(),
                            s.getDocument(),
                            s.getPhone()))
                    .collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_proveedores.xlsx");
            excelReportService.generateExcel(response, "Proveedores", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}