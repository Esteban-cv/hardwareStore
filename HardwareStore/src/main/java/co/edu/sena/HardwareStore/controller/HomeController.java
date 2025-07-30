package co.edu.sena.HardwareStore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    @GetMapping("/")
    public String showlogin() {
        return "home/login";
    }

    // Página de login
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

    // Página después del login exitoso
    @GetMapping("/home")
    public String dashboard() {
        return "home/index";
    }

    @GetMapping("/logout")
    public String logOut() {return "home/logout";}
}
