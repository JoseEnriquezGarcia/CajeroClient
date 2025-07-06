package com.JEnriquez.Crud.Controller;

import com.JEnriquez.Crud.ML.Cantidad;
import com.JEnriquez.Crud.ML.Monto;
import com.JEnriquez.Crud.ML.Result;
import com.JEnriquez.Crud.ML.Usuario;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/home")
public class CajeroController {

    private RestTemplate restTemplate = new RestTemplate();
    private String urlBase = "http://localhost:8081/cajero";

    @GetMapping
    public String GetAllMonedas(Model model, HttpSession session) {
        try {
            if (session != null && session.getAttribute("username") != null) {
                Monto monto = new Monto();

                Usuario usuario = new Usuario();
                usuario.setUsername(session.getAttribute("username").toString());
                usuario.setPassword(session.getAttribute("password").toString());

                HttpHeaders header = new HttpHeaders();
                header.setBasicAuth(usuario.getUsername(), usuario.getPassword());

                HttpEntity<String> entity = new HttpEntity<>(header);

                ResponseEntity<Result<Usuario>> responseUsuario = restTemplate.exchange(urlBase + "/usuario/" + usuario.getUsername(),
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<Result<Usuario>>() {
                });
                
                List<String> headers = responseUsuario.getHeaders().get("X-Auth-Rol");
                String Rol = headers.get(0);
                
                ResponseEntity<Result> responseMontoTotal = restTemplate.exchange(urlBase + "/cantidadTotal",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<Result>() {
                });

                Result result = new Result();
                result = responseMontoTotal.getBody();

                Result resultUsuario = responseUsuario.getBody();

                model.addAttribute("cantidad", result.object);
                model.addAttribute("monto", monto);
                model.addAttribute("usuario", resultUsuario.object);
                model.addAttribute("rol", Rol);
            } else {
                return "redirect:/login";
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "Index";
    }

    @GetMapping("/deposito")
    public String Deposito(Model model, HttpSession session) {
        try {
            if (session != null && session.getAttribute("username") != null) {
                Monto monto = new Monto();
                Usuario usuario = new Usuario();
                usuario.setUsername(session.getAttribute("username").toString());
                usuario.setPassword(session.getAttribute("password").toString());

                HttpHeaders header = new HttpHeaders();
                header.setBasicAuth(usuario.getUsername(), usuario.getPassword());

                HttpEntity<String> entity = new HttpEntity<>(header);
                ResponseEntity<Result<Usuario>> responseUsuario = restTemplate.exchange(urlBase + "/usuario/" + usuario.getUsername(),
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<Result<Usuario>>() {
                });

                Result resultUsuario = responseUsuario.getBody();

                model.addAttribute("usuario", resultUsuario.object);
                model.addAttribute("monto", monto);
            } else {
                return "redirect:/login";
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "Deposito";
    }

    @PostMapping("/procesarDeposito")
    public String ProcesarDeposito(@ModelAttribute Monto monto, Model model, HttpSession session) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsername(session.getAttribute("username").toString());
            usuario.setPassword(session.getAttribute("password").toString());

            HttpHeaders header = new HttpHeaders();
            header.setBasicAuth(usuario.getUsername(), usuario.getPassword());
            HttpEntity<String> entity = new HttpEntity<>(header);

            ResponseEntity<Result> response = restTemplate.exchange(urlBase + "/deposito/" + monto.getMonto() + "/" + usuario.getUsername(),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Result>() {
            });

        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }

        return "redirect:/home";
    }

    @GetMapping("/llenarCajero")
    public String LlenarCajero(Model model, HttpSession session) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsername(session.getAttribute("username").toString());
            usuario.setPassword(session.getAttribute("password").toString());

            HttpHeaders header = new HttpHeaders();
            header.setBasicAuth(usuario.getUsername(), usuario.getPassword());

            HttpEntity<String> entity = new HttpEntity<>(header);
            ResponseEntity<Result> response = restTemplate.exchange(urlBase + "/llenarCajero",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Result>() {
            });

        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "redirect:/home";
    }

    @PostMapping("/retirar")
    public String retirarEfectivo(@ModelAttribute Monto monto, Model model, HttpSession session) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsername(session.getAttribute("username").toString());
            usuario.setPassword(session.getAttribute("password").toString());
            Monto montoRetiro = new Monto();

            HttpHeaders header = new HttpHeaders();
            header.setBasicAuth(usuario.getUsername(), usuario.getPassword());
            HttpEntity<String> entity = new HttpEntity<>(header);

            ResponseEntity<Result<Map<Double, Integer>>> response = restTemplate.exchange(urlBase + "/retirar/" + monto.getMonto() + "/" + usuario.getUsername(),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Result<Map<Double, Integer>>>() {
            });

            ResponseEntity<Result> responseMontoTotal = restTemplate.exchange(urlBase + "/cantidadTotal",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Result>() {
            });

            ResponseEntity<Result<Usuario>> responseUsuario = restTemplate.exchange(urlBase + "/usuario/" + usuario.getUsername(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Result<Usuario>>() {
            });

            Result<Map<Double, Integer>> result = new Result();
            result = response.getBody();

            Result resultEfectivoTotal = new Result();
            resultEfectivoTotal = responseMontoTotal.getBody();

            Result resultUsuario = responseUsuario.getBody();

            Map<Double, Integer> efectivo = result.objects.get(0);

            model.addAttribute("cantidad", resultEfectivoTotal.object);
            model.addAttribute("efectivoEntregado", efectivo);
            model.addAttribute("monto", montoRetiro);
            model.addAttribute("usuario", resultUsuario.object);
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }

        return "Index";
    }
}
