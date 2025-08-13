package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Entry;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.EntryRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/entries")
public class EntryController {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PdfReportService pdfReportService;

    @Autowired
    private ExcelReportService excelReportService;

    /**
     * Lista todas las entradas ordenadas por fecha descendente
     */
    @GetMapping
    public String listEntry(Model model) {
        List<Entry> entries = entryRepository.findAll(Sort.by("dateEntry").descending());
        model.addAttribute("entries", entries);
        return "inventory/entries";
    }

    /**
     * Muestra el formulario de creación/edición de entrada
     */
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("entry", new Entry());
        model.addAttribute("articles", articleRepository.findAll());
        return "inventory/entry_form";
    }

    /**
     * Guarda una entrada nueva o actualizada
     */
    @PostMapping("/save")
    public String save(@ModelAttribute Entry entry, RedirectAttributes ra) {
        try {
            boolean esNuevo = (entry.getIdEntry() == null);
            entryRepository.save(entry);

            ra.addFlashAttribute("success",
                    esNuevo ? "Entrada creada exitosamente" : "Entrada actualizada exitosamente");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la entrada: " + e.getMessage());
        }
        return "redirect:/entries";
    }

    /**
     * Carga una entrada existente para edición
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idEntry, Model model, RedirectAttributes ra) {
        Entry entry = entryRepository.findById(idEntry).orElse(null);
        if (entry == null) {
            ra.addFlashAttribute("error", "Entrada no encontrada");
            return "redirect:/entries";
        }

        model.addAttribute("entry", entry);
        model.addAttribute("articles", articleRepository.findAll());
        return "inventory/entry_form";
    }

    /**
     * Elimina una entrada
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idEntry, RedirectAttributes ra) {
        try {
            entryRepository.deleteById(idEntry);
            ra.addFlashAttribute("success", "Entrada eliminada exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar la entrada: " + e.getMessage());
        }
        return "redirect:/entries";
    }

    /**
     * Genera un reporte PDF de entradas
     */
    @GetMapping("/entryreport")
    public void generateEntryReport(HttpServletResponse response) throws IOException {
        try {
            List<Entry> entries = entryRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha", "Cantidad", "Observaciones", "Artículo");

            List<List<String>> rows = entries.stream()
                    .map(entry -> Arrays.asList(
                            String.valueOf(entry.getIdEntry()),
                            entry.getDateEntry().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            String.valueOf(entry.getQuantity()),
                            entry.getObservations() != null ? entry.getObservations() : "",
                            entry.getArticle() != null ? entry.getArticle().getName() : "N/A"
                    ))
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_entradas.pdf");
            pdfReportService.generatePdf(response, "Reporte de Entradas", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte PDF: " + e.getMessage());
        }
    }

    /**
     * Genera un reporte Excel de entradas
     */
    @GetMapping("/entryreport/excel")
    public void generateEntryExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Entry> entries = entryRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha", "Cantidad", "Observaciones", "Artículo");

            List<List<String>> rows = entries.stream()
                    .map(entry -> Arrays.asList(
                            String.valueOf(entry.getIdEntry()),
                            entry.getDateEntry().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            String.valueOf(entry.getQuantity()),
                            entry.getObservations() != null ? entry.getObservations() : "",
                            entry.getArticle() != null ? entry.getArticle().getName() : "N/A"
                    ))
                    .collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_entradas.xlsx");
            excelReportService.generateExcel(response, "Entradas", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}
