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

@Controller
public class HomeController {

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

    @GetMapping("/")
    public String showLogin() {
        return "home/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }

        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión correctamente");
        }

        return "home/login";
    }

    @GetMapping("/home")
    public String dashboard(Model model) {
        // Opción 1: Los 10 clientes que más han gastado
        List<Object[]> topSpendingClients = clientRepository.findTopSpendingClients();

        // Opción 2: Los 10 clientes más recientes
        List<Object[]> recentClientsData = clientRepository.findRecentClientsWithTotalSpent();

        // Usar los clientes que más han gastado para mostrar primero
        model.addAttribute("recentClients", topSpendingClients);
        model.addAttribute("clientsTitle", "Top 10 Clientes que Más Han Comprado");

        // Stats para las cards
        long totalClients = clientRepository.countClients();
        long activeClients = dashboardService.getActiveClients();
        double activeClientsPercent = dashboardService.getActiveClientsPercent();

        model.addAttribute("totalClients", totalClients);
        model.addAttribute("totalArticles", articleRepository.countArticles());
        model.addAttribute("totalIssues", issueRepository.countIssues());
        model.addAttribute("totalEntries", entryRepository.countEntries());

        // Datos para clientes activos
        model.addAttribute("activeClients", activeClients);
        model.addAttribute("activeClientsPercent", activeClientsPercent);

        // Datos para el gráfico de crecimiento de clientes
        model.addAttribute("clientChartLabels", dashboardService.getClientChartLabels());
        model.addAttribute("clientChartData", dashboardService.getClientChartData());

        return "home/index";
    }

    @GetMapping("/logout")
    public String logOut() {
        return "home/logout";
    }
}