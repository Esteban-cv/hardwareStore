package co.edu.sena.HardwareStore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import co.edu.sena.HardwareStore.repository.ArticleRepository;
import co.edu.sena.HardwareStore.repository.ClientRepository;
import co.edu.sena.HardwareStore.repository.EntryRepository;
import co.edu.sena.HardwareStore.repository.IssueRepository;
import co.edu.sena.HardwareStore.services.DashboardService;

@Controller // Indica que esta clase es un controlador de Spring MVC que maneja peticiones HTTP
public class HomeController {

    // Inyección de dependencias para acceder a los repositorios y servicios
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private EntryRepository entryRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/") // Mapea la URL raíz "/" a este método
    public String showLogin() {
        // Retorna la vista de login ubicada en templates/home/login.html
        return "home/login";
    }

    @GetMapping("/login") // Mapea la ruta "/login"
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        // Si existe un parámetro "error", se añade un mensaje al modelo
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }

        // Si existe un parámetro "logout", se añade un mensaje de cierre de sesión
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión correctamente");
        }

        // Retorna nuevamente la vista de login
        return "home/login";
    }

    @GetMapping("/home") // Mapea la ruta "/home"
    public String dashboard(Model model) {
        // Consulta: Top 10 clientes que más han gastado
        List<Object[]> topSpendingClients = clientRepository.findTopSpendingClients();

        // Consulta: Top 10 clientes más recientes con total gastado
        List<Object[]> recentClientsData = clientRepository.findRecentClientsWithTotalSpent();

        // Por defecto se usan los clientes que más han gastado
        model.addAttribute("recentClients", topSpendingClients);
        model.addAttribute("clientsTitle", "Top 10 Clientes que Más Han Comprado");

        // Estadísticas para las tarjetas del dashboard
        long totalClients = clientRepository.countClients(); // Total de clientes
        long activeClients = dashboardService.getActiveClients(); // Clientes activos
        double activeClientsPercent = dashboardService.getActiveClientsPercent(); // Porcentaje activos

        // Añadir datos al modelo para la vista
        model.addAttribute("totalClients", totalClients);
        model.addAttribute("totalArticles", articleRepository.countArticles());
        model.addAttribute("totalIssues", issueRepository.countIssues());
        model.addAttribute("totalEntries", entryRepository.countEntries());

        model.addAttribute("activeClients", activeClients);
        model.addAttribute("activeClientsPercent", activeClientsPercent);

        // Datos para el gráfico de crecimiento de clientes
        model.addAttribute("clientChartLabels", dashboardService.getClientChartLabels());
        model.addAttribute("clientChartData", dashboardService.getClientChartData());

        // Retorna la vista del dashboard ubicada en templates/home/index.html
        return "home/index";
    }

    @GetMapping("/logout") // Mapea la ruta "/logout"
    public String logOut() {
        // Retorna la vista de logout ubicada en templates/home/logout.html
        return "home/logout";
    }
}
