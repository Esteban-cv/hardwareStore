package co.edu.sena.HardwareStore.controller;

// Importaciones de entidades, repositorios y servicios necesarios
import co.edu.sena.HardwareStore.model.Issue;
import co.edu.sena.HardwareStore.repository.IssueRepository;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.EmployeeRepository;
import co.edu.sena.HardwareStore.repository.ClientRepository;
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

@Controller // Indica que esta clase es un controlador de Spring MVC
@RequestMapping("/issue") // Todas las rutas aquí tendrán el prefijo "/issue"
public class IssueController {

    // Inyección de dependencias de los repositorios y servicios
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

    // Método GET para listar todas las salidas (issues) ordenadas por fecha descendente
    @GetMapping
    public String listIssues(Model model) {
        List<Issue> issues = issueRepository.findAll(Sort.by("dateIssue").descending());
        model.addAttribute("issues", issues); // Envía la lista a la vista
        return "inventory/issue"; // Retorna la plantilla HTML
    }

    // Método GET para mostrar el formulario de creación de salida
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("issue", new Issue()); // Objeto vacío para el formulario
        model.addAttribute("articles", articleRepository.findAll()); // Lista de artículos
        model.addAttribute("employees", employeeRepository.findAll()); // Lista de empleados
        model.addAttribute("clients", clientRepository.findAll()); // Lista de clientes
        return "inventory/issue_form";
    }

    // Método POST para guardar o actualizar una salida
    @PostMapping("/save")
    public String save(@ModelAttribute Issue issue, RedirectAttributes ra) {
         try {
            boolean esNuevo = (issue.getIdIssue() == null); // Verifica si es nuevo o edición
            issueRepository.save(issue); // Guarda en BD

            // Mensaje según el caso
            if (esNuevo) {
                ra.addFlashAttribute("success", "Salida creada exitosamente");
            } else {
                ra.addFlashAttribute("success", "Salida actualizada exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la salida");
        }
        return "redirect:/issue"; // Redirige a la lista
    }

    // Método GET para editar una salida existente
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long idIssue, Model model, RedirectAttributes ra) {
        Issue issue = issueRepository.findById(idIssue).orElse(null); // Busca por ID
        if (issue == null) { // Si no existe, redirige con error
            ra.addFlashAttribute("error", "Salida no encontrada");
            return "redirect:/issue";
        }

        // Agrega los datos al modelo para precargar el formulario
        model.addAttribute("issue", issue);
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        return "inventory/issue_form";
    }

    // Método POST para eliminar una salida
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idIssue, RedirectAttributes ra) {
        issueRepository.deleteById(idIssue); // Elimina de la BD
        ra.addFlashAttribute("success", "Salida eliminada exitosamente");
        return "redirect:/issue";
    }

    // Método GET para generar reporte PDF de todas las salidas
    @GetMapping("/issuereport")
    public void generateIssueReport(HttpServletResponse response) throws IOException {
        try {
            List<Issue> issues = issueRepository.findAll(); // Obtiene todas las salidas
            List<String> headers = Arrays.asList("ID", "Fecha", "Cantidad", "Observaciones", "Artículo", "Cliente", "Empleado");

            // Convierte los datos de Issue a listas de Strings
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

            // Configura la respuesta como PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_salidas.pdf");

            // Llama al servicio para generar el PDF
            pdfReportService.generatePdf(response, "Reporte de Salidas", headers, rows);

        } catch (Exception e) {
            // En caso de error, envía mensaje al cliente
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    // Método GET para generar reporte Excel de todas las salidas
    @GetMapping("/issuereport/excel")
    public void generateIssueExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Issue> issues = issueRepository.findAll(); // Obtiene todas las salidas
            List<String> headers = Arrays.asList("ID", "Fecha", "Cantidad", "Observaciones", "Artículo", "Cliente", "Empleado");

            // Convierte los datos de Issue a listas de Strings
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

            // Configura la respuesta como archivo Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_salidas.xlsx");

            // Llama al servicio para generar el Excel
            excelReportService.generateExcel(response, "Salidas", headers, rows);

        } catch (Exception e) {
            // En caso de error, envía mensaje al cliente
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}
