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

/**
 * Controlador encargado de gestionar las operaciones relacionadas con los proveedores.
 * Permite listar, crear, editar, eliminar y generar reportes en PDF y Excel.
 *
 * <p>Las vistas relacionadas se encuentran en la carpeta {@code suppliers} dentro de templates.</p>
 *
 * @author
 */
@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    /** Repositorio para gestionar los artículos y sus consultas. */
    @Autowired
    private ArticleRepository articleRepository;

    /** Repositorio para gestionar los proveedores y sus consultas. */
    @Autowired
    private SupplierRepository supplierRepository;

    /** Servicio para la generación de reportes en formato PDF. */
    @Autowired
    private PdfReportService pdfReportService;

    /** Servicio para la generación de reportes en formato Excel. */
    @Autowired
    private ExcelReportService excelReportService;

    /**
     * Muestra la lista de proveedores ordenados por ID de forma descendente.
     *
     * @param model Modelo de datos para la vista.
     * @return Nombre de la plantilla HTML para mostrar la lista de proveedores.
     */
    @GetMapping
    public String listSuppliers(Model model) {
        List<Supplier> suppliers = supplierRepository.findAll(Sort.by("idSupplier").descending());
        model.addAttribute("suppliers", suppliers);
        return "suppliers/supplier";
    }

    /**
     * Muestra el formulario para crear un nuevo proveedor.
     *
     * @param model Modelo de datos para la vista.
     * @return Nombre de la plantilla HTML del formulario de proveedor.
     */
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "suppliers/supplier_form";
    }

    /**
     * Guarda o actualiza un proveedor.
     *
     * @param supplier Objeto proveedor enviado desde el formulario.
     * @param ra       Atributos para enviar mensajes flash a la vista.
     * @return Redirección a la lista de proveedores.
     */
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

    /**
     * Muestra el formulario de edición para un proveedor específico.
     *
     * @param idSupplier ID del proveedor a editar.
     * @param model      Modelo de datos para la vista.
     * @param ra         Atributos para mensajes flash.
     * @return Nombre de la plantilla HTML para edición o redirección en caso de error.
     */
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

    /**
     * Elimina un proveedor si no tiene artículos asociados.
     *
     * @param idSupplier ID del proveedor a eliminar.
     * @param ra         Atributos para mensajes flash.
     * @return Redirección a la lista de proveedores.
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idSupplier, RedirectAttributes ra) {
        try {
            Supplier supplier = supplierRepository.findById(idSupplier)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

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

    /**
     * Genera un reporte PDF con la información de todos los proveedores.
     *
     * @param response Objeto HTTP para enviar el archivo como descarga.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
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

    /**
     * Genera un reporte Excel con la información de todos los proveedores.
     *
     * @param response Objeto HTTP para enviar el archivo como descarga.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
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
