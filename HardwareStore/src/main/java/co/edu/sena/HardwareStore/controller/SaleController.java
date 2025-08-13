package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Article;
import co.edu.sena.HardwareStore.model.Sale;
import co.edu.sena.HardwareStore.model.SaleDetail;
import co.edu.sena.HardwareStore.repository.*;
import co.edu.sena.HardwareStore.services.ExcelReportService;
import co.edu.sena.HardwareStore.services.PdfReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con las ventas.
 * Incluye creación, edición, eliminación, visualización de detalles y generación de reportes.
 */
@Controller
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PdfReportService pdfReportService;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ExcelReportService excelReportService;

    /**
     * Lista todas las ventas ordenadas por fecha descendente.
     * @param model Modelo para enviar datos a la vista.
     * @return Vista con la lista de ventas.
     */
    @GetMapping
    public String listSales(Model model) {
        List<Sale> sales = saleRepository.findAll(Sort.by("date").descending());
        model.addAttribute("sales", sales);
        return "sales/sale";
    }

    /**
     * Muestra el formulario para registrar una nueva venta.
     * @param model Modelo para enviar datos a la vista.
     * @return Vista del formulario de ventas.
     */
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("sale", new Sale());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("articles", articleRepository.findAll());
        return "sales/sale_form";
    }

    /**
     * Guarda una nueva venta y sus detalles.
     * @param sale Objeto de venta.
     * @param articleIds Lista de IDs de artículos.
     * @param quantities Lista de cantidades.
     * @param prices Lista de precios unitarios.
     * @param ra Atributos para redirección con mensajes.
     * @return Redirección a la lista de ventas.
     */
    @PostMapping("/save")
    @Transactional
    public String save(@ModelAttribute Sale sale,
            @RequestParam(value = "articleIds", required = false) List<Integer> articleIds,
            @RequestParam(value = "quantities", required = false) List<Integer> quantities,
            @RequestParam(value = "prices", required = false) List<BigDecimal> prices,
            RedirectAttributes ra) {
        // Lógica original
        try {
            if (articleIds == null || articleIds.isEmpty()) {
                ra.addFlashAttribute("error", "No se pueden procesar ventas sin productos");
                return "redirect:/sales/form";
            }
            if (articleIds.size() != quantities.size() || articleIds.size() != prices.size()) {
                ra.addFlashAttribute("error", "Error en los datos de productos");
                return "redirect:/sales/form";
            }
            if (sale.getDate() == null) {
                sale.setDate(LocalDate.now());
            }

            for (int i = 0; i < articleIds.size(); i++) {
                Integer articleId = articleIds.get(i);
                Integer quantity = quantities.get(i);
                Article article = articleRepository.findById(articleId)
                        .orElseThrow(() -> new RuntimeException("Artículo no encontrado: " + articleId));
                if (article.getQuantity() < quantity) {
                    ra.addFlashAttribute("error",
                            "Stock insuficiente para el artículo: " + article.getName() +
                                    ". Disponible: " + article.getQuantity() + ", Solicitado: " + quantity);
                    return "redirect:/sales/form";
                }
            }

            BigDecimal subTotal = calculateSubTotal(articleIds, quantities, prices);
            BigDecimal tax = subTotal.multiply(new BigDecimal("0.19")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subTotal.add(tax);

            sale.setSubTotal(subTotal);
            sale.setTax(tax);
            sale.setTotal(total);

            Sale savedSale = saleRepository.save(sale);
            processSaleDetails(savedSale, articleIds, quantities, prices);

            ra.addFlashAttribute("success", "Venta procesada exitosamente. Total: $" + total);
            return "redirect:/sales";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al procesar la venta: " + e.getMessage());
            return "redirect:/sales/form";
        }
    }

    /**
     * Muestra el formulario para editar una venta existente.
     * @param id ID de la venta.
     * @param model Modelo para enviar datos a la vista.
     * @param ra Atributos para redirección.
     * @return Vista de edición o redirección si no existe.
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Sale sale = saleRepository.findById(id).orElse(null);
        if (sale == null) {
            ra.addFlashAttribute("error", "Venta no encontrada");
            return "redirect:/sales";
        }

        if (sale.getDate() == null) {
            sale.setDate(LocalDate.now());
        }

        model.addAttribute("sale", sale);
        return "sales/edit_sale_form";
    }

    /**
     * Actualiza la información básica de una venta.
     * @param id ID de la venta.
     * @param date Nueva fecha.
     * @param observations Observaciones adicionales.
     * @param ra Atributos para redirección.
     * @return Redirección a la lista de ventas.
     */
    @PostMapping("/edit/{id}")
    @Transactional
    public String updateSale(@PathVariable("id") Long id,
            @RequestParam("date") LocalDate date,
            @RequestParam(value = "observations", required = false) String observations,
            RedirectAttributes ra) {
        try {
            Sale sale = saleRepository.findById(id).orElse(null);
            if (sale == null) {
                ra.addFlashAttribute("error", "Venta no encontrada");
                return "redirect:/sales";
            }

            sale.setDate(date);
            sale.setObservations(observations);
            saleRepository.save(sale);

            ra.addFlashAttribute("success", "Venta actualizada correctamente");
            return "redirect:/sales";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar la venta: " + e.getMessage());
            return "redirect:/sales";
        }
    }

    /**
     * Obtiene los detalles de una venta en formato JSON.
     * @param id ID de la venta.
     * @return Respuesta con los datos de la venta y sus detalles.
     */
    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewSaleDetails(@PathVariable("id") Long id) {
        try {
            Sale sale = saleRepository.findById(id).orElse(null);
            if (sale == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("type", "sale");
            response.put("idSale", sale.getIdSale());
            response.put("date", sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            response.put("clientName", sale.getClient() != null ? sale.getClient().getName() : "N/A");
            response.put("employeeName", sale.getEmployee() != null ? sale.getEmployee().getName() : "N/A");
            response.put("subTotal", sale.getSubTotal());
            response.put("tax", sale.getTax());
            response.put("total", sale.getTotal());
            response.put("observations", sale.getObservations());

            List<SaleDetail> details = saleDetailRepository.findBySaleId(id);
            List<Map<String, Object>> detailsList = details.stream().map(detail -> {
                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("articleName", detail.getArticle().getName());
                detailMap.put("articleCode", detail.getArticle().getCode());
                detailMap.put("quantity", detail.getQuantity());
                detailMap.put("unitPrice", detail.getUnitPrice());
                detailMap.put("total", detail.getTotal());
                return detailMap;
            }).collect(Collectors.toList());

            response.put("details", detailsList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Elimina una venta, restaura el stock y borra los detalles asociados.
     * @param id ID de la venta.
     * @param ra Atributos para redirección.
     * @return Redirección a la lista de ventas.
     */
    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            Sale sale = saleRepository.findById(id).orElse(null);
            if (sale == null) {
                ra.addFlashAttribute("error", "Venta no encontrada");
                return "redirect:/sales";
            }

            List<SaleDetail> details = saleDetailRepository.findBySaleId(id);
            for (SaleDetail detail : details) {
                Article article = detail.getArticle();
                article.setQuantity(article.getQuantity() + detail.getQuantity());
                articleRepository.save(article);
            }

            saleDetailRepository.deleteAll(details);
            saleRepository.deleteById(id);

            ra.addFlashAttribute("success", "Venta eliminada exitosamente y stock restaurado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar la venta: " + e.getMessage());
        }
        return "redirect:/sales";
    }

    /**
     * Genera un reporte PDF con todas las ventas.
     * @param response Respuesta HTTP para descarga del archivo.
     * @throws IOException En caso de error de escritura.
     */
    @GetMapping("/salereport")
    public void generateSaleReport(HttpServletResponse response) throws IOException {
        try {
            List<Sale> sales = saleRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha","IVA","Subtotal", "Total", "Cliente", "Empleado");
            List<List<String>> rows = sales.stream()
                    .map(s -> {
                        String fechaFormateada = s.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdSale()),
                                fechaFormateada,
                                String.valueOf(s.getTax()),
                                String.valueOf(s.getSubTotal()),
                                String.valueOf(s.getTotal()),
                                s.getClient() != null ? s.getClient().getName() : "N/A",
                                s.getEmployee() != null ? s.getEmployee().getName() : "N/A");
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

    /**
     * Genera un reporte Excel con todas las ventas.
     * @param response Respuesta HTTP para descarga del archivo.
     * @throws IOException En caso de error de escritura.
     */
    @GetMapping("/salereport/excel")
    public void generateSaleExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Sale> sales = saleRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Fecha","IVA","Subtotal", "Total", "Cliente", "Empleado");
            List<List<String>> rows = sales.stream()
                    .map(s -> {
                        String fechaFormateada = s.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return Arrays.asList(
                                String.valueOf(s.getIdSale()),
                                fechaFormateada,
                                String.valueOf(s.getTax()),
                                String.valueOf(s.getSubTotal()),
                                String.valueOf(s.getTotal()),
                                s.getClient() != null ? s.getClient().getName() : "N/A",
                                s.getEmployee() != null ? s.getEmployee().getName() : "N/A");
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

    /**
     * Calcula el subtotal de una venta.
     * @param articleIds IDs de los artículos.
     * @param quantities Cantidades por artículo.
     * @param prices Precios unitarios.
     * @return Subtotal calculado.
     */
    private BigDecimal calculateSubTotal(List<Integer> articleIds, List<Integer> quantities, List<BigDecimal> prices) {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (int i = 0; i < articleIds.size(); i++) {
            BigDecimal unitPrice = prices.get(i);
            Integer quantity = quantities.get(i);
            BigDecimal detailTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            subTotal = subTotal.add(detailTotal);
        }
        return subTotal;
    }

    /**
     * Procesa y guarda los detalles de una venta, actualizando el inventario.
     * @param savedSale Venta guardada.
     * @param articleIds IDs de artículos vendidos.
     * @param quantities Cantidades vendidas.
     * @param prices Precios unitarios.
     */
    private void processSaleDetails(Sale savedSale, List<Integer> articleIds, List<Integer> quantities,
            List<BigDecimal> prices) {
        for (int i = 0; i < articleIds.size(); i++) {
            Integer articleId = articleIds.get(i);
            Integer quantity = quantities.get(i);
            BigDecimal unitPrice = prices.get(i);

            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setSale(savedSale);
            saleDetail.setArticle(article);
            saleDetail.setQuantity(quantity);
            saleDetail.setUnitPrice(unitPrice);
            saleDetail.setTotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));

            saleDetailRepository.save(saleDetail);

            article.setQuantity(article.getQuantity() - quantity);
            articleRepository.save(article);
        }
    }
}
