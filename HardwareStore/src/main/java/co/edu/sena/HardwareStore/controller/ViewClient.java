package co.edu.sena.HardwareStore.controller;

import co.edu.sena.HardwareStore.model.Client;
import co.edu.sena.HardwareStore.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViewClient {
    @Autowired
    ClientRepository clientRepository;

    @GetMapping("/clients")
    public String list(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        return  "client";
    }

    @GetMapping("/client/form")
    public String form(Model model) {
        model.addAttribute("client", new Client());
        return "client_form";
    }

    @PostMapping("/client/save")
    public String save(@ModelAttribute Client client, RedirectAttributes ra) {
        clientRepository.save(client);
        ra.addFlashAttribute("success", "Cliente guardado exitosamente.");
        return "redirect:/clients";
    }

    @GetMapping("/client/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Client client = clientRepository.findById(id).orElse(null);
        model.addAttribute("client", client);
        return "client_form";
    }

    @PostMapping("/client/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        clientRepository.deleteById(id);
        ra.addFlashAttribute("success", "Cliente elimianado exitosamente.");
        return "redirect:/clients";
    }
}
