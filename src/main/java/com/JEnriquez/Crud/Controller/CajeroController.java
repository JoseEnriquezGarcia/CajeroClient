package com.JEnriquez.Crud.Controller;

import com.JEnriquez.Crud.ML.Cantidad;
import com.JEnriquez.Crud.ML.Monto;
import com.JEnriquez.Crud.ML.Result;
import com.JEnriquez.Crud.ML.TipoMoneda;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/home")
public class CajeroController {

    private RestTemplate restTemplate = new RestTemplate();
    private String urlBase = "http://localhost:8081/cajero";

    @GetMapping("/info")
    public String GetAllMonedas(Model model) {
        try {
            Monto monto = new Monto();

            ResponseEntity<Result<TipoMoneda>> response = restTemplate.exchange(urlBase,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<TipoMoneda>>() {
            });

            ResponseEntity<Result<Cantidad>> responseCantidad = restTemplate.exchange(urlBase + "/cantidad",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Cantidad>>() {
            });

            ResponseEntity<Result> responseMontoTotal = restTemplate.exchange(urlBase + "/cantidadTotal",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });
            Result result = new Result();
            result = response.getBody();

            Result resultDenominaciones = new Result();
            result = responseCantidad.getBody();

            Result resultMontoTotal = new Result();
            resultMontoTotal = responseMontoTotal.getBody();

            model.addAttribute("tiposMonedas", result.objects);
            model.addAttribute("Denominaciones", resultDenominaciones.objects);
            model.addAttribute("cantidad", resultMontoTotal.object);
            model.addAttribute("monto", monto);

        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "Index";
    }

    @GetMapping("/llenarCajero")
    public String LlenarCajero(Model model) {
        try {
            ResponseEntity<Result> response = restTemplate.exchange(urlBase + "/llenarCajero",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });

        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "redirect:/home/info";
    }

    @PostMapping("/retirar")
    public String retirarEfectivo(@ModelAttribute Monto monto, Model model) {
        try {
            Monto montoRetiro = new Monto();
            ResponseEntity<Result<Map<Double, Integer>>> response = restTemplate.exchange(urlBase + "/retirar/" + monto.getMonto(),
                    HttpMethod.POST,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Map<Double, Integer>>>() {
            });
            
            
            ResponseEntity<Result> responseMontoTotal = restTemplate.exchange(urlBase + "/cantidadTotal",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });

            Result<Map<Double, Integer>> result = new Result();
            result = response.getBody();

            Result resultEfectivoTotal = new Result();
            resultEfectivoTotal = responseMontoTotal.getBody();
            
            Map<Double, Integer> efectivo = result.objects.get(0);
            
            model.addAttribute("cantidad", resultEfectivoTotal.object);
            model.addAttribute("efectivoEntregado", efectivo);
            model.addAttribute("monto", montoRetiro);
            
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }

        return "Index";
    }
}
