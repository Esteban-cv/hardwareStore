package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.Entry;
import co.edu.sena.HardwareStore.model.Purchase;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.EntryRepository;
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
@RequestMapping("/entries")
public class EntryController {
    @Autowired
    private EntryRepository entryRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private PdfReportService pdfReportService;
    @GetMapping
    public String listEntry(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Entry> entries = entryRepository.findAll(PageRequest.of(page, 10, Sort.by("dateEntry").descending()));
        model.addAttribute("entries", entries);
        return "inventory/entries";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("entry", new Entry());
        model.addAttribute("articles", articleRepository.findAll());
        return "inventory/entry_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Entry entry, RedirectAttributes ra){
        entryRepository.save(entry);
        ra.addFlashAttribute("success", "Entrada guardada exitosamente");
        return "redirect:/entries";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idEntry, Model model, RedirectAttributes ra){
        Entry entry = entryRepository.findById(idEntry).orElse(null);
        if(entry == null){
            ra.addFlashAttribute("error", "Entrada no encontrada");
            return "redirect:/entries";
        }

        model.addAttribute("entry", entry);
        model.addAttribute("articles", articleRepository.findAll());
        return "inventory/entry_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idEntry, RedirectAttributes ra){
        entryRepository.deleteById(idEntry);
        ra.addFlashAttribute("success", "Entrada eliminada exitosamente");
        return "redirect:/entries";
    }

    @GetMapping("/entryreport")
    public void generateentryReport(HttpServletResponse response) throws IOException {
        try {
            List<Entry> entries = entryRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha", "Cantidad", "Observaciones", "Artículo");
            List<List<String>> rows = entries.stream()
                    .map(s -> {
                        // Formatear fecha (ejemplo para java.util.Date)
                        String fechaFormateada = s.getDateEntry().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdEntry()),
                                fechaFormateada,
                                String.valueOf(s.getQuantity()),
                                String.valueOf(s.getObservations()),
                                s.getArticle() != null ? s.getArticle().getName() : "N/A"
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_entradas.pdf");
            pdfReportService.generatePdf(response, "Reporte de Entradas", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

}
