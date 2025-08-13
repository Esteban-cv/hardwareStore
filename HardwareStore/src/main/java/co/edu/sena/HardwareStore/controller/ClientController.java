package co.edu.sena.HardwareStore.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import co.edu.sena.HardwareStore.model.Client;
import co.edu.sena.HardwareStore.repository.ClientRepository;
import co.edu.sena.HardwareStore.services.ExcelReportService;
import co.edu.sena.HardwareStore.services.PdfReportService;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador para la gestión de clientes en el sistema.
 * Permite realizar operaciones CRUD y generar reportes en PDF y Excel.
 */
@Controller
@RequestMapping("/clients")
public class ClientController {

    /** Repositorio para el manejo de datos de clientes. */
    @Autowired
    private ClientRepository clientRepository;

    /** Servicio para la generación de reportes PDF. */
    @Autowired
    private PdfReportService pdfReportService;

    /** Servicio para la generación de reportes Excel. */
    @Autowired
    private ExcelReportService excelReportService;

    /**
     * Lista todos los clientes en orden descendente por su ID.
     * 
     * @param model Objeto para enviar datos a la vista.
     * @return Nombre de la vista con la lista de clientes.
     */
    @GetMapping
    public String listClient(Model model) {
        List<Client> clients = clientRepository.findAll(Sort.by("idClient").descending());
        model.addAttribute("clients", clients);
        return "client/clients";
    }

    /**
     * Muestra el formulario para crear o editar un cliente.
     * 
     * @param model Objeto para enviar datos a la vista.
     * @return Nombre de la vista del formulario de cliente.
     */
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("client", new Client());
        return "client/client_form";
    }

    /**
     * Guarda o actualiza un cliente en la base de datos.
     * 
     * @param client Objeto cliente a guardar o actualizar.
     * @param ra Objeto para enviar mensajes flash.
     * @return Redirección a la lista de clientes.
     */
    @PostMapping("/save")
    public String save(@ModelAttribute Client client, RedirectAttributes ra) {
        try {
            boolean esNuevo = (client.getIdClient() == null);

            if (esNuevo) {
                clientRepository.save(client);
                ra.addFlashAttribute("success", "Cliente creado exitosamente");
            } else {
                Client existente = clientRepository.findById(client.getIdClient())
                        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

                existente.setDocument(client.getDocument());
                existente.setName(client.getName());
                existente.setPhone(client.getPhone());
                existente.setEmail(client.getEmail());
                existente.setRut(client.getRut());

                clientRepository.save(existente);
                ra.addFlashAttribute("success", "Cliente actualizado exitosamente");
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar Cliente: " + e.getMessage());
        }

        return "redirect:/clients";
    }

    /**
     * Muestra el formulario para editar un cliente existente.
     * 
     * @param idClient ID del cliente a editar.
     * @param model Objeto para enviar datos a la vista.
     * @param ra Objeto para enviar mensajes flash.
     * @return Nombre de la vista del formulario de cliente o redirección.
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long idClient, Model model, RedirectAttributes ra) {
        Client client = clientRepository.findById(idClient).orElse(null);
        if (client == null) {
            ra.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/clients";
        }
        model.addAttribute("client", client);

        return "client/client_form";
    }

    /**
     * Elimina un cliente por su ID.
     * 
     * @param idClient ID del cliente a eliminar.
     * @param ra Objeto para enviar mensajes flash.
     * @return Redirección a la lista de clientes.
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long idClient, RedirectAttributes ra) {
        clientRepository.deleteById(idClient);
        ra.addFlashAttribute("success", "Cliente eliminado exitosamente");
        return "redirect:/clients";
    }

    /**
     * Genera un reporte PDF con la lista de clientes.
     * 
     * @param response Objeto HTTP para enviar el archivo.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @GetMapping("/clientreport")
    public void generateClientReport(HttpServletResponse response) throws IOException {
        try {
            List<Client> clients = clientRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Documento", "Nombre", "Teléfono", "Email", "rut");
            List<List<String>> rows = clients.stream()
                    .map(c -> {
                        return Arrays.asList(
                                String.valueOf(c.getIdClient()),
                                c.getDocument(),
                                c.getName(),
                                c.getPhone(),
                                c.getEmail(),
                                c.getRut() != null ? c.getRut() : "N/A");
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_clientes.pdf");
            pdfReportService.generatePdf(response, "Reporte de Clientes", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    /**
     * Genera un reporte Excel con la lista de clientes.
     * 
     * @param response Objeto HTTP para enviar el archivo.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @GetMapping("/clientreport/excel")
    public void generateClientExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Client> clients = clientRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Documento", "Nombre", "Teléfono", "Email", "rut");
            List<List<String>> rows = clients.stream()
                    .map(c -> {
                        return Arrays.asList(
                                String.valueOf(c.getIdClient()),
                                c.getDocument(),
                                c.getName(),
                                c.getPhone(),
                                c.getEmail(),
                                c.getRut() != null ? c.getRut() : "N/A");
                    })
                    .collect(Collectors.toList());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_clientes.xlsx");

            excelReportService.generateExcel(response, "Clientes", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }

}
