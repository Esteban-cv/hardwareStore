package co.edu.sena.HardwareStore.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.sena.HardwareStore.repository.ClientRepository;
import co.edu.sena.HardwareStore.repository.SaleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private SaleRepository saleRepository;

    /**
     * Obtiene datos para el gráfico de crecimiento de clientes (últimos 12 meses)
     */
    public List<String> getClientChartLabels() {
        LocalDateTime from = LocalDateTime.now().minusMonths(12);
        List<Object[]> data = clientRepository.countByMonthSince(from);
        
        List<String> labels = new ArrayList<>();
        for (Object[] row : data) {
            labels.add((String) row[0]); // formato YYYY-MM
        }
        
        return labels;
    }

    public List<Integer> getClientChartData() {
        LocalDateTime from = LocalDateTime.now().minusMonths(12);
        List<Object[]> data = clientRepository.countByMonthSince(from);
        
        List<Integer> chartData = new ArrayList<>();
        for (Object[] row : data) {
            chartData.add(((Number) row[1]).intValue()); // cantidad de clientes
        }
        
        return chartData;
    }

    /**
     * Obtiene el número de clientes activos (con ventas en últimos 6 meses)
     */
    public long getActiveClients() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return saleRepository.countActiveClientsSince(sixMonthsAgo);
    }

    /**
     * Calcula el porcentaje de clientes activos
     */
    public double getActiveClientsPercent() {
        long totalClients = clientRepository.countClients();
        long activeClients = getActiveClients();
        
        if (totalClients == 0) return 0;
        
        return Math.round((double) activeClients / totalClients * 100 * 100.0) / 100.0;
    }
}