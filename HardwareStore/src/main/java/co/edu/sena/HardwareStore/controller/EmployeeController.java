package co.edu.sena.HardwareStore.controller; // Paquete donde se encuentra la clase

// Importación de clases y librerías necesarias
import co.edu.sena.HardwareStore.model.Employee; // Modelo Employee
import co.edu.sena.HardwareStore.model.Role; // Modelo Role
import co.edu.sena.HardwareStore.repository.EmployeeRepository; // Repositorio para Employee
import co.edu.sena.HardwareStore.repository.RoleRepository; // Repositorio para Role
import co.edu.sena.HardwareStore.services.ExcelReportService; // Servicio para generar reportes Excel
import co.edu.sena.HardwareStore.services.PdfReportService; // Servicio para generar reportes PDF
import jakarta.servlet.http.HttpServletResponse; // Manejo de respuesta HTTP
import org.springframework.beans.factory.annotation.Autowired; // Inyección de dependencias
import org.springframework.data.domain.Sort; // Para ordenar resultados
import org.springframework.stereotype.Controller; // Anotación de controlador
import org.springframework.ui.Model; // Objeto para enviar datos a la vista
import org.springframework.web.bind.annotation.*; // Anotaciones de rutas
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Mensajes flash

import java.io.IOException; // Manejo de excepciones de entrada/salida
import java.util.Arrays; // Para trabajar con listas fijas
import java.util.List; // Colecciones de listas
import java.util.stream.Collectors; // Operaciones con Streams

@Controller // Indica que esta clase es un controlador de Spring MVC
@RequestMapping("/employees") // Prefijo de todas las rutas de este controlador
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository; // Inyección del repositorio de empleados

    @Autowired
    private RoleRepository RoleRepository; // Inyección del repositorio de roles
    
    @Autowired
    private PdfReportService pdfReportService; // Servicio para reportes PDF

    @Autowired
    private ExcelReportService excelReportService; // Servicio para reportes Excel

    @GetMapping // Maneja solicitudes GET a "/employees"
    public String listEmployees(Model model) {
        // Obtiene todos los empleados ordenados por ID de forma ascendente
        List<Employee> employees = employeeRepository.findAll(Sort.by("idEmployee").ascending());
        model.addAttribute("employees", employees); // Agrega la lista de empleados al modelo
        return "employees/employee"; // Retorna la vista "employee"
    }

    @GetMapping("/form") // Ruta para mostrar el formulario de empleado
    public String form(Model model) {
        model.addAttribute("employee", new Employee()); // Crea un nuevo empleado vacío
        model.addAttribute("roles", RoleRepository.findAll()); // Agrega todos los roles al modelo
        return "employees/employee_form"; // Retorna la vista del formulario
    }

    @PostMapping("/save") // Ruta para guardar un empleado (nuevo o editado)
    public String save(@ModelAttribute Employee employee, @RequestParam Long roleId, RedirectAttributes ra) {
         try {
            boolean esNuevo = (employee.getIdEmployee() == null); // Verifica si es un nuevo empleado

            // Asigna el rol seleccionado al empleado
            employee.setRole(RoleRepository.findById(roleId).orElse(null));
            employeeRepository.save(employee); // Guarda el empleado en la base de datos

            // Mensajes de éxito dependiendo si es nuevo o actualización
            if (esNuevo) {
                ra.addFlashAttribute("success", "Empleado creado exitosamente");
            } else {
                ra.addFlashAttribute("success", "Empleado actualizado exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el empleado"); // Mensaje de error
        }
        return "redirect:/employees"; // Redirige a la lista de empleados
    }

    @GetMapping("/edit/{id}") // Ruta para editar un empleado
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            // Busca el empleado por ID, lanza excepción si no existe
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new Exception("Empleado no encontrado"));
            model.addAttribute("employee", employee); // Agrega el empleado al modelo

            // Obtiene todos los roles ordenados por nombre ascendente
            List<Role> roles = RoleRepository.findAll(Sort.by("name").ascending());
            model.addAttribute("roles", roles);

            return "employees/employee_form"; // Muestra el formulario
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Empleado no encontrado"); // Mensaje de error
            return "redirect:/employees"; // Redirige a la lista
        }
    }

    @PostMapping("/delete/{id}") // Ruta para eliminar un empleado
    public String delete(@PathVariable("id") Long idEmployee, RedirectAttributes ra) {
        try {
            employeeRepository.deleteById(idEmployee); // Elimina por ID
            ra.addFlashAttribute("success", "Empleado eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar empleado: " + e.getMessage());
        }
        return "redirect:/employees"; // Redirige a la lista
    }

    @GetMapping("/employeereport") // Ruta para generar reporte PDF
    public void generateEmployeeReport(HttpServletResponse response) throws IOException {
        try {
            // Obtiene todos los empleados
            List<Employee> employees = employeeRepository.findAll();
            // Define encabezados de la tabla
            List<String> headers = Arrays.asList("ID", "Nombre", "Usuario", "Contraseña");
            // Convierte los empleados en listas de cadenas
            List<List<String>> rows = employees.stream()
                    .map(s -> Arrays.asList(
                            String.valueOf(s.getIdEmployee()),
                            s.getName(),
                            s.getUser(),
                            s.getPassword()))
                    .collect(Collectors.toList());

            // Configura la respuesta HTTP como PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_empleados.pdf");
            pdfReportService.generatePdf(response, "Reporte de Empleados", headers, rows); // Genera PDF

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Error 500
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @GetMapping("/employeereport/excel") // Ruta para generar reporte Excel
    public void generateEmployeeExcelReport(HttpServletResponse response) throws IOException {
        try {
            // Obtiene todos los empleados
            List<Employee> employees = employeeRepository.findAll();
            // Define encabezados de la tabla
            List<String> headers = Arrays.asList("ID", "Nombre", "Usuario", "Contraseña");
            // Convierte los empleados en listas de cadenas
            List<List<String>> rows = employees.stream()
                    .map(s -> Arrays.asList(
                            String.valueOf(s.getIdEmployee()),
                            s.getName(),
                            s.getUser(),
                            s.getPassword()))
                    .collect(Collectors.toList());

            // Configura la respuesta HTTP como Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_empleados.xlsx");
            excelReportService.generateExcel(response, "Empleados", headers, rows); // Genera Excel

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Error 500
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}
