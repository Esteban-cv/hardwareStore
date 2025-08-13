package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Location;
import co.edu.sena.HardwareStore.repository.LocationRepository;
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
@RequestMapping("/locations")
public class LocationController {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private PdfReportService pdfReportService;
    @Autowired
    private ExcelReportService excelReportService;
    @GetMapping
    public String listLocations(Model model) {
        List<Location> locations = locationRepository.findAll(Sort.by("idLocation").descending());
        model.addAttribute("locations", locations);
        return "inventory/locations";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("location", new Location());
        return "inventory/location_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Location location, RedirectAttributes ra){
        try {
            boolean esNuevo = (location.getIdLocation() == null);

            locationRepository.save(location);

            if (esNuevo) {
                ra.addFlashAttribute("success", "Ubicación creada exitosamente");
            } else {
                ra.addFlashAttribute("success", "Ubicación actualizada exitosamente");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la ubicación");
        }
        return "redirect:/locations";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer idLocation, Model model, RedirectAttributes ra) {
        Location location = locationRepository.findById(idLocation).orElse(null);
        if (location == null) {
            ra.addFlashAttribute("error", "Ubicación no encontrada.");
            return "redirect:/locations";
        }
        model.addAttribute("location", location);
        return "inventory/location_form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer idLocation, RedirectAttributes ra){
        locationRepository.deleteById(idLocation);
        ra.addFlashAttribute("success", "Ubicación eliminada exitosamente");
        return "redirect:/locations";
    }

    @GetMapping("/locationreport")
    public void generateSaleReport(HttpServletResponse response) throws IOException {
        try {
            List<Location> locations = locationRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre", "Código");
            List<List<String>> rows = locations.stream()
                    .map(s -> {
                        // Formatear fecha (ejemplo para java.util.Date)
                        return Arrays.asList(
                                String.valueOf(s.getIdLocation()),
                                String.valueOf(s.getName()),
                                String.valueOf(s.getCode())
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_ubicaciones.pdf");
            pdfReportService.generatePdf(response, "Reporte de Ubicaciones", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @GetMapping("/locationreport/excel")
    public void generateLocationExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Location> locations = locationRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre", "Código");
            List<List<String>> rows = locations.stream()
                    .map(s -> Arrays.asList(
                            String.valueOf(s.getIdLocation()),
                            s.getName(),
                            s.getCode()
                    ))
                    .collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_ubicaciones.xlsx");

            excelReportService.generateExcel(response, "Ubicaciones", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}
