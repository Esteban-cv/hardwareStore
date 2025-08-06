package co.edu.sena.HardwareStore.controller;


import co.edu.sena.HardwareStore.model.Article;
import co.edu.sena.HardwareStore.model.Category;
import co.edu.sena.HardwareStore.model.Unit;
import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.CategoryRepository;
import co.edu.sena.HardwareStore.repository.UnitRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Controller
@RequestMapping("/catalog")
public class CatalogController {

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
    public String listArticles(@RequestParam(defaultValue = "0") int page, Model model){
        Page<Article> articles = articleRepository.findAll(PageRequest.of(page,10, Sort.by("idArticle").ascending()));
        articles.forEach(p ->{
            if (p.getPrice() != null) {
                p.setPrice(p.getPrice().setScale(2, RoundingMode.HALF_UP));
            }
        });
        model.addAttribute("articles", articles);
        return "catalog/articles";
    }

    @GetMapping("/categories")
    public String listCategories(@RequestParam(defaultValue = "0") int page,Model model){
        Page<Category> categories = categoryRepository.findAll(PageRequest.of(page,10,Sort.by("idCategory").ascending()));
        model.addAttribute("categories", categories);
        return "catalog/categories";
    }

    @GetMapping("/article/form")
    public String formArticle(Model model){
        model.addAttribute("article", new Article());
        model.addAttribute("category", new Category());
        model.addAttribute("unit", new Unit());
        return "catalog/article_form";
    }

    @GetMapping("/category/form")
    public String formCategory(Model model){
        model.addAttribute("category", new Category());
        return "catalog/category_form";
    }

    @PostMapping("/save/article")
    public String saveArticle(@ModelAttribute Article article, RedirectAttributes ra){
        articleRepository.save(article);
        ra.addFlashAttribute("success", "Artículo guardado exitosamente");
        return "redirect:/articles";
    }

    @PostMapping("/save/category")
    public String saveCategory(@ModelAttribute Category category, RedirectAttributes ra){
        categoryRepository.save(category);
        ra.addFlashAttribute("success", "Categoria guardada exitosamente");
        return "redirect:/categories";
    }

    @GetMapping("/editArticle/{id}")
    public String editArticle(@PathVariable("id") Integer idArticle, Model model, RedirectAttributes ra){
        Article article = articleRepository.findById(idArticle).orElse(null);
        if (article == null){
            ra.addFlashAttribute("error", "Artículo no encontrado");
            return "redirect:/articles";
        }
        model.addAttribute("article", article);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        return "catalog/article_form";
    }

    @GetMapping("/editCategory/{id}")
    public String edit(@PathVariable Integer idCategory, Model model, RedirectAttributes ra){
        Category category = categoryRepository.findById(idCategory).orElse(null);
        if(category == null){
            ra.addFlashAttribute("error", "Categoria no encontrada");
            return "redirect:/categories";
        }
        model.addAttribute("category", category);
        return "catalog/category_form";
    }

    @PostMapping("/deleteArticle/{id}")
    public String deleteArticle(@PathVariable("id") Integer idArticle, RedirectAttributes ra){
        articleRepository.deleteById(idArticle);
        ra.addFlashAttribute("success", "Artículo eliminado exitosamente");
        return "redirect:/articles";
    }

    @PostMapping("/deleteCategory/{id}")
    public String deleteCategories(@PathVariable("id") Integer idCategory, RedirectAttributes ra){
        categoryRepository.deleteById(idCategory);
        ra.addFlashAttribute("success", "Categoria eliminada exitosamente");
        return "redirect:/categories";
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

            // Configurar respuesta HTTP
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_articulos.pdf");

            // Generar PDF
            pdfReportService.generatePdf(response, "Reporte de Artículos", headers, rows);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error al generar el reporte: " + e.getMessage());
        }
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
