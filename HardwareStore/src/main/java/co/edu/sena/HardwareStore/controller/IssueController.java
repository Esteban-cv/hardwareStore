package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Issue;
import co.edu.sena.HardwareStore.repository.IssueRepository;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.EmployeeRepository;
import co.edu.sena.HardwareStore.repository.ClientRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/issue")
public class IssueController {

    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PdfReportService pdfReportService;
    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping
    public String listIssues(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Issue> issues = issueRepository.findAll(PageRequest.of(page, 10, Sort.by("dateIssue").descending()));
        model.addAttribute("issues", issues);
        return "inventory/issue";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("issue", new Issue());
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        return "inventory/issue_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Issue issue, RedirectAttributes ra) {
        issueRepository.save(issue);
        ra.addFlashAttribute("success", "Salida guardada exitosamente");
        return "redirect:/issue";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long idIssue, Model model, RedirectAttributes ra) {
        Issue issue = issueRepository.findById(idIssue).orElse(null);
        if (issue == null) {
            ra.addFlashAttribute("error", "Salida no encontrada");
            return "redirect:/issue";
        }

        
        model.addAttribute("issue", issue);
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        return "inventory/issue_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idIssue, RedirectAttributes ra) {
        issueRepository.deleteById(idIssue);
        ra.addFlashAttribute("success", "Salida eliminada exitosamente");
        return "redirect:/issue";
    }

    @GetMapping("/issuereport")
    public void generateIssueReport(HttpServletResponse response) throws IOException {
        try {
            List<Issue> issues = issueRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha", "Cantidad", "Observaciones", "Artículo", "Cliente", "Empleado");
            List<List<String>> rows = issues.stream()
                    .map(s -> {
                        String fechaFormateada = s.getDateIssue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdIssue()),
                                fechaFormateada,
                                String.valueOf(s.getQuantity()),
                                s.getObservations(),
                                s.getArticle() != null ? s.getArticle().getName() : "N/A",
                                s.getClient() != null ? s.getClient().getName() : "N/A",
                                s.getEmployee() != null ? s.getEmployee().getName() : "N/A"
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_salidas.pdf");
            pdfReportService.generatePdf(response, "Reporte de Salidas", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @GetMapping("/issuereport/excel")
    public void generateIssueExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Issue> issues = issueRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha", "Cantidad", "Observaciones", "Artículo", "Cliente", "Empleado");
            List<List<String>> rows = issues.stream()
                    .map(s -> {
                        String fechaFormateada = s.getDateIssue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdIssue()),
                                fechaFormateada,
                                String.valueOf(s.getQuantity()),
                                s.getObservations(),
                                s.getArticle() != null ? s.getArticle().getName() : "N/A",
                                s.getClient() != null ? s.getClient().getName() : "N/A",
                                s.getEmployee() != null ? s.getEmployee().getName() : "N/A"
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_salidas.xlsx");
            excelReportService.generateExcel(response, "Salidas", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}