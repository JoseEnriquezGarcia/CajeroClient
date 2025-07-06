package com.JEnriquez.Crud.Controller;

import com.JEnriquez.Crud.ML.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        return "Login";
    }

    @PostMapping("/validation")
    public String Process(@ModelAttribute Usuario usuario, HttpSession session) {
        session.setAttribute("username", usuario.getUsername());
        session.setAttribute("password", usuario.getPassword());
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String Logout(HttpSession session) {
        session.removeAttribute("username");
        session.removeAttribute("password");
        return "redirect:/login";
    }
}
