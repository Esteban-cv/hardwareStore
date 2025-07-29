package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Location;
import co.edu.sena.HardwareStore.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/locations")
public class LocationController {
    @Autowired
    private LocationRepository locationRepository;

    @GetMapping
    public String list(Model model){
        model.addAttribute("locations", locationRepository.findAll());
        return "inventory/locations";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("locations", new Location());
        return "inventory/location_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Location location, RedirectAttributes ra){
        locationRepository.save(location);
        ra.addFlashAttribute("success", "Ubicación guardada exitosamente.");
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
}
