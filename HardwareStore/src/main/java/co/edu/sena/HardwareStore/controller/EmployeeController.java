package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Employee;
import co.edu.sena.HardwareStore.model.Role;
import co.edu.sena.HardwareStore.repository.EmployeeRepository;
import co.edu.sena.HardwareStore.repository.RoleRepository;
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
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository RoleRepository;
    @Autowired
    private PdfReportService pdfReportService;

    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping
    public String listEmployees(Model model) {
        List<Employee> employees = employeeRepository.findAll(Sort.by("idEmployee").ascending());
        model.addAttribute("employees", employees);
        return "employees/employee";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("roles", RoleRepository.findAll());
        return "employees/employee_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Employee employee, @RequestParam Long roleId, RedirectAttributes ra) {
         try {
            boolean esNuevo = (employee.getIdEmployee() == null);

            employee.setRole(RoleRepository.findById(roleId).orElse(null));
            employeeRepository.save(employee);

            if (esNuevo) {
                ra.addFlashAttribute("success", "Empleado creado exitosamente");
            } else {
                ra.addFlashAttribute("success", "Empleado actualizado exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el empleado");
        }
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new Exception("Empleado no encontrado"));
            model.addAttribute("employee", employee);

         
            List<Role> roles = RoleRepository.findAll(Sort.by("name").ascending());
            model.addAttribute("roles", roles);

            return "employees/employee_form";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Empleado no encontrado");
            return "redirect:/employees";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idEmployee, RedirectAttributes ra) {
        try {
            employeeRepository.deleteById(idEmployee);
            ra.addFlashAttribute("success", "Empleado eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar empleado: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    @GetMapping("/employeereport")
    public void generateEmployeeReport(HttpServletResponse response) throws IOException {
        try {
            List<Employee> employees = employeeRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre", "Usuario", "Contraseña");
            List<List<String>> rows = employees.stream()
                    .map(s -> Arrays.asList(
                            String.valueOf(s.getIdEmployee()),
                            s.getName(),
                            s.getUser(),
                            s.getPassword()))
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_empleados.pdf");
            pdfReportService.generatePdf(response, "Reporte de Empleados", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @GetMapping("/employeereport/excel")
    public void generateEmployeeExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Employee> employees = employeeRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre", "Usuario", "Contraseña");
            List<List<String>> rows = employees.stream()
                    .map(s -> Arrays.asList(
                            String.valueOf(s.getIdEmployee()),
                            s.getName(),
                            s.getUser(),
                            s.getPassword()))
                    .collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_empleados.xlsx");
            excelReportService.generateExcel(response, "Empleados", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}
