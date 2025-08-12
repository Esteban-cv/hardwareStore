package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.Article;
import co.edu.sena.HardwareStore.model.Category;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.CategoryRepository;
import co.edu.sena.HardwareStore.repository.SupplierRepository;
import co.edu.sena.HardwareStore.repository.UnitRepository;
import co.edu.sena.HardwareStore.services.ExcelReportService;
import co.edu.sena.HardwareStore.services.PdfReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PdfReportService pdfReportService;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private ExcelReportService excelReportService;


    @GetMapping("/articles")
    public String listArticles(Model model) {
        model.addAttribute("articles", articleRepository.findAll());
        return "catalog/articles";  
    }

    @GetMapping("/article/form")
    public String formArticle(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "catalog/article_form";
    }

    @PostMapping("/article/save")
    public String saveArticle(@ModelAttribute Article article, RedirectAttributes ra) {
        try {
            articleRepository.save(article);
            ra.addFlashAttribute("success", "Artículo guardado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Hubo un error al guardar el artículo.");
        }
        return "redirect:/catalog/articles";
    }

    @GetMapping("/editArticle/{id}")
    public String editArticle(@PathVariable("id") Integer idArticle, Model model, RedirectAttributes ra) {
        Article article = articleRepository.findById(idArticle).orElse(null);
        if (article == null) {
            ra.addFlashAttribute("error", "Artículo no encontrado");
            return "redirect:/catalog/articles";
        }
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "catalog/article_form";
    }

    @PostMapping("/deleteArticle/{id}")
    public String deleteArticle(@PathVariable("id") Integer idArticle, RedirectAttributes ra) {
        try {
            articleRepository.deleteById(idArticle);
            ra.addFlashAttribute("success", "Artículo guardado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Hubo un error al guardar el artículo.");
        }
        return "redirect:/catalog/articles";
    }

    @GetMapping("/articlereport")
    public void generateArticleReport(HttpServletResponse response) throws IOException {
        try {
            List<Article> articles = articleRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre", "Código", "Cantidad", "Precio", "Categoría", "Unidad");
            List<List<String>> rows = articles.stream()
                    .map(article -> {
                        String precioFormateado = article.getPrice() != null
                                ? String.format("$%,.2f", article.getPrice())
                                : "$0.00";
                        return Arrays.asList(
                                String.valueOf(article.getIdArticle()),
                                article.getName() != null ? article.getName() : "N/A",
                                article.getCode() != null ? article.getCode() : "N/A",
                                String.valueOf(article.getQuantity()),
                                precioFormateado,
                                article.getCategory() != null ? article.getCategory().getName() : "Sin categoría",
                                article.getUnit() != null ? article.getUnit().getName() : "Sin unidad"
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_articulos.pdf");
            pdfReportService.generatePdf(response, "Reporte de Artículos", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    // ---------- CATEGORIES ------------------

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "catalog/categories";
    }

    @GetMapping("/category/form")
    public String formCategory(Model model) {
        model.addAttribute("category", new Category());
        return "catalog/category_form";
    }

    @PostMapping("/save/category")
    public String saveCategory(@ModelAttribute Category category, RedirectAttributes ra) {
        categoryRepository.save(category);
        ra.addFlashAttribute("success", "Categoría guardada exitosamente");
        return "redirect:/catalog/categories";
    }

    @GetMapping("/editCategory/{idCategory}")
    public String editCategory(@PathVariable("idCategory") Integer id, Model model, RedirectAttributes ra) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            ra.addFlashAttribute("error", "Categoría no encontrada");
            return "redirect:/catalog/categories";
        }
        model.addAttribute("category", category);
        return "catalog/category_form";
    }

    @PostMapping("/deleteCategory/{idCategory}")
    public String deleteCategory(@PathVariable("idCategory") Integer id, RedirectAttributes ra) {
        categoryRepository.deleteById(id);
        ra.addFlashAttribute("success", "Categoría eliminada exitosamente");
        return "redirect:/catalog/categories";
    }

    @GetMapping("/categoryreport")
    public void generateCategoryReport(HttpServletResponse response) throws IOException {
        try {
            List<Category> categories = categoryRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre");
            List<List<String>> rows = categories.stream()
                    .map(category -> Arrays.asList(
                            String.valueOf(category.getIdCategory()),
                            category.getName() != null ? category.getName() : "N/A"
                    ))
                    .collect(Collectors.toList());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_categorias.pdf");
            pdfReportService.generatePdf(response, "Reporte de Categorías", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @GetMapping("/articlereport/excel")
    public void generateArticleExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Article> articles = articleRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre", "Código", "Cantidad", "Precio", "Categoría", "Unidad");
            List<List<String>> rows = articles.stream()
                    .map(article -> {
                        String precioFormateado = article.getPrice() != null
                                ? String.format("$%,.2f", article.getPrice())
                                : "$0.00";
                        return Arrays.asList(
                                String.valueOf(article.getIdArticle()),
                                article.getName() != null ? article.getName() : "N/A",
                                article.getCode() != null ? article.getCode() : "N/A",
                                String.valueOf(article.getQuantity()),
                                precioFormateado,
                                article.getCategory() != null ? article.getCategory().getName() : "Sin categoría",
                                article.getUnit() != null ? article.getUnit().getName() : "Sin unidad"
                        );
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_articulos.xlsx");

            excelReportService.generateExcel(response, "Artículos", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }

    @GetMapping("/categoryreport/excel")
    public void generateCategoryExcelReport(HttpServletResponse response) throws IOException {
        try {
            List<Category> categories = categoryRepository.findAll();
            List<String> headers = Arrays.asList("ID", "Nombre");
            List<List<String>> rows = categories.stream()
                    .map(category -> Arrays.asList(
                            String.valueOf(category.getIdCategory()),
                            category.getName() != null ? category.getName() : "N/A"
                    ))
                    .collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_categorias.xlsx");

            excelReportService.generateExcel(response, "Categorías", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte Excel: " + e.getMessage());
        }
    }
}

