package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Client;
import co.edu.sena.HardwareStore.model.Employee;
import co.edu.sena.HardwareStore.model.Sale;
import co.edu.sena.HardwareStore.repository.*;
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
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private PdfReportService pdfReportService;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping
    public String listSales(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Sale> sales = saleRepository.findAll(PageRequest.of(page, 10, Sort.by("date").descending()));
        model.addAttribute("sales", sales);
        return "sales/sale";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("sale", new Sale());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        return "sales/sale_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Sale sale, RedirectAttributes ra) {
        saleRepository.save(sale);
        ra.addFlashAttribute("success", "Venta guardada exitosamente");
        return "redirect:/sales";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Sale sale = saleRepository.findById(id).orElse(null);
        if (sale == null) {
            ra.addFlashAttribute("error", "Venta no encontrada");
            return "redirect:/sales";
        }
        model.addAttribute("sale", sale);
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        return "sales/sale_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        saleRepository.deleteById(id);
        ra.addFlashAttribute("success", "Venta eliminada exitosamente");
        return "redirect:/sales";
    }

    @GetMapping("/salereport")
    public void generateSaleReport(HttpServletResponse response) throws IOException {
        try {
            List<Sale> sales = saleRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha", "Total", "Cliente", "Empleado");
            List<List<String>> rows = sales.stream()
                    .map(s -> {
                        // Formatear fecha (ejemplo para java.util.Date)
                        String fechaFormateada = s.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdSale()),
                                fechaFormateada,
                                String.valueOf(s.getTotal()),
                                s.getClient() != null ? s.getClient().getName() : "N/A",
                                s.getEmployee() != null ? s.getEmployee().getName() : "N/A"
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_ventas.pdf");
            pdfReportService.generatePdf(response, "Reporte de Ventas", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @GetMapping("/salereport/excel")
    public void generateSaleExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Sale> sales = saleRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha", "Total", "Cliente", "Empleado");
            List<List<String>> rows = sales.stream()
                    .map(s -> {
                        String fechaFormateada = s.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdSale()),
                                fechaFormateada,
                                String.valueOf(s.getTotal()),
                                s.getClient() != null ? s.getClient().getName() : "N/A",
                                s.getEmployee() != null ? s.getEmployee().getName() : "N/A"
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_ventas.xlsx");

            excelReportService.generateExcel(response, "Ventas", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }



}
