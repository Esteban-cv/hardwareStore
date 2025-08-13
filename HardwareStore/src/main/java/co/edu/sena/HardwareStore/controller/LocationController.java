package co.edu.sena.HardwareStore.controller; // Paquete donde se encuentra la clase

// Importaciones necesarias para el controlador
import co.edu.sena.HardwareStore.model.Location; // Modelo de la entidad Location
import co.edu.sena.HardwareStore.repository.LocationRepository; // Repositorio JPA de Location
import co.edu.sena.HardwareStore.services.ExcelReportService; // Servicio para generar reportes en Excel
import co.edu.sena.HardwareStore.services.PdfReportService; // Servicio para generar reportes en PDF
import jakarta.servlet.http.HttpServletResponse; // Manejo de respuestas HTTP
import org.springframework.beans.factory.annotation.Autowired; // Inyección de dependencias
import org.springframework.data.domain.Sort; // Ordenamiento de consultas
import org.springframework.stereotype.Controller; // Anotación para controladores MVC
import org.springframework.ui.Model; // Pasar datos a la vista
import org.springframework.web.bind.annotation.*; // Anotaciones de rutas HTTP
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Enviar mensajes en redirecciones

import java.io.IOException; // Manejo de excepciones de entrada/salida
import java.util.Arrays; // Utilidad para trabajar con arrays
import java.util.List; // Colecciones List
import java.util.stream.Collectors; // Transformaciones con Streams

@Controller // Indica que es un controlador Spring MVC
@RequestMapping("/locations") // Ruta base para todas las peticiones del controlador
public class LocationController {

    @Autowired
    private LocationRepository locationRepository; // Repositorio para operaciones CRUD en Location

    @Autowired
    private PdfReportService pdfReportService; // Servicio para reportes PDF

    @Autowired
    private ExcelReportService excelReportService; // Servicio para reportes Excel

    @GetMapping // Maneja GET en "/locations"
    public String listLocations(Model model) {
        // Obtiene todas las ubicaciones ordenadas por ID de forma descendente
        List<Location> locations = locationRepository.findAll(Sort.by("idLocation").descending());
        model.addAttribute("locations", locations); // Pasa la lista a la vista
        return "inventory/locations"; // Retorna la vista correspondiente
    }

    @GetMapping("/form") // Maneja GET en "/locations/form"
    public String form(Model model){
        model.addAttribute("location", new Location()); // Crea un nuevo objeto Location vacío
        return "inventory/location_form"; // Retorna el formulario de ubicación
    }

    @PostMapping("/save") // Maneja POST en "/locations/save"
    public String save(@ModelAttribute Location location, RedirectAttributes ra){
        try {
            // Verifica si es nuevo o actualización
            boolean esNuevo = (location.getIdLocation() == null);

            // Guarda la ubicación en la base de datos
            locationRepository.save(location);

            // Mensaje de éxito dependiendo de la operación
            if (esNuevo) {
                ra.addFlashAttribute("success", "Ubicación creada exitosamente");
            } else {
                ra.addFlashAttribute("success", "Ubicación actualizada exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la ubicación"); // Mensaje de error
        }
        return "redirect:/locations"; // Redirige a la lista de ubicaciones
    }

    @GetMapping("/edit/{id}") // Maneja GET en "/locations/edit/{id}"
    public String edit(@PathVariable("id") Integer idLocation, Model model, RedirectAttributes ra) {
        // Busca la ubicación por su ID
        Location location = locationRepository.findById(idLocation).orElse(null);
        if (location == null) { // Si no existe, muestra error
            ra.addFlashAttribute("error", "Ubicación no encontrada.");
            return "redirect:/locations"; // Redirige a la lista
        }
        model.addAttribute("location", location); // Pasa la ubicación a la vista
        return "inventory/location_form"; // Retorna el formulario
    }

    @PostMapping("/delete/{id}") // Maneja POST en "/locations/delete/{id}"
    public String delete(@PathVariable("id") Integer idLocation, RedirectAttributes ra){
        locationRepository.deleteById(idLocation); // Elimina la ubicación por ID
        ra.addFlashAttribute("success", "Ubicación eliminada exitosamente"); // Mensaje de éxito
        return "redirect:/locations"; // Redirige a la lista
    }

    @GetMapping("/locationreport") // Genera reporte PDF de ubicaciones
    public void generateSaleReport(HttpServletResponse response) throws IOException {
        try {
            // Obtiene todas las ubicaciones
            List<Location> locations = locationRepository.findAll();
            // Encabezados del reporte
            List<String> headers = Arrays.asList("ID", "Nombre", "Código");
            // Filas del reporte con datos
            List<List<String>> rows = locations.stream()
                    .map(s -> Arrays.asList(
                            String.valueOf(s.getIdLocation()),
                            String.valueOf(s.getName()),
                            String.valueOf(s.getCode())
                    ))
                    .collect(Collectors.toList());

            // Configura la respuesta como PDF descargable
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_ubicaciones.pdf");

            // Genera el PDF con los datos
            pdfReportService.generatePdf(response, "Reporte de Ubicaciones", headers, rows);

        } catch (Exception e) {
            // Manejo de errores al generar el reporte
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @GetMapping("/locationreport/excel") // Genera reporte Excel de ubicaciones
    public void generateLocationExcelReport(HttpServletResponse response) throws IOException {
        try {
            // Obtiene todas las ubicaciones
            List<Location> locations = locationRepository.findAll();
            // Encabezados del reporte
            List<String> headers = Arrays.asList("ID", "Nombre", "Código");
            // Filas con los datos
            List<List<String>> rows = locations.stream()
                    .map(s -> Arrays.asList(
                            String.valueOf(s.getIdLocation()),
                            s.getName(),
                            s.getCode()
                    ))
                    .collect(Collectors.toList());

            // Configura la respuesta como Excel descargable
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_ubicaciones.xlsx");

            // Genera el archivo Excel
            excelReportService.generateExcel(response, "Ubicaciones", headers, rows);

        } catch (Exception e) {
            // Manejo de errores al generar el reporte Excel
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}
