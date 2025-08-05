package co.edu.sena.HardwareStore.services;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class PdfReportService {

    @Autowired
    private TemplateEngine templateEngine;

    public void generatePdf(HttpServletResponse response, String title, List<String> headers, List<List<String>> rows) throws IOException {
        // 1. Preparar datos
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("headers", headers);
        context.setVariable("rows", rows);

        // 2. Procesar plantilla Thymeleaf
        String htmlContent = templateEngine.process("reports/report-base", context);

        // 3. Renderizar HTML a PDF
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        // 4. Escribir a la respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + title.replace(" ", "_") + ".pdf");

        OutputStream outputStream = response.getOutputStream();
        renderer.createPDF(outputStream);
        outputStream.close();
    }
}
