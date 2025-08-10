package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Supplier;
import co.edu.sena.HardwareStore.repository.SupplierRepository;
import co.edu.sena.HardwareStore.services.ExcelReportService;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/suppliers")  // Cambié de /supplier a /suppliers para consistencia
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private PdfReportService pdfReportService;
    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping
    public String listSuppliers(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Supplier> suppliers = supplierRepository.findAll(
            PageRequest.of(page, 10, Sort.by("idSupplier").descending()));
        model.addAttribute("suppliers", suppliers);
        return "suppliers/supplier"; // Corregida la ruta de la vista
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "suppliers/supplier_form"; // Corregida la ruta de la vista
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Supplier supplier, RedirectAttributes ra) {
        try {
            supplierRepository.save(supplier);
            ra.addFlashAttribute("success", "Proveedor guardado exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el proveedor");
        }
        return "redirect:/suppliers"; // Corregida la ruta de redirección
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
            return "redirect:/suppliers"; // plural
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idSupplier, RedirectAttributes ra) {
        try {
            supplierRepository.deleteById(idSupplier);
            ra.addFlashAttribute("success", "Proveedor eliminado exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar el proveedor");
        }
        return "redirect:/suppliers"; // Corregida la ruta de redirección
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
                            s.getPhone()
                    ))
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
                            s.getPhone()
                    ))
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