package com.unir.biblioteca.operador;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operador")
//@CrossOrigin(origins = "*")
public class OperadorController {


    @GetMapping("/ping")
    public String ping() {
        return "MS Operador funcionando";
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "OK", "service", "operador-service");
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return List.of(
                Map.of("id", 1, "nombre", "Operador 1", "activo", true),
                Map.of("id", 2, "nombre", "Operador 2", "activo", false)
        );
    }

    @GetMapping("/{id}")
    public Map<String, Object> obtener(@PathVariable int id) {
        return Map.of(
                "id", id,
                "nombre", "Operador " + id,
                "activo", true
        );
    }

    // ---- Acciones de biblioteca (dummy, recibiendo body) ----

    @PostMapping("/prestar")
    public Map<String, Object> prestar(@RequestBody Map<String, Object> body) {
        return Map.of(
                "accion", "prestar",
                "status", "OK",
                "mensaje", "Préstamo simulado",
                "datosRecibidos", body,
                "timestamp", System.currentTimeMillis()
        );
    }

    @PutMapping("/extender")
    public Map<String, Object> extender(@RequestBody Map<String, Object> body) {
        return Map.of(
                "accion", "extender",
                "status", "OK",
                "mensaje", "Extensión simulada",
                "datosRecibidos", body,
                "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/devolver")
    public Map<String, Object> devolver(@RequestBody Map<String, Object> body) {
        return Map.of(
                "accion", "devolver",
                "status", "OK",
                "mensaje", "Devolución simulada",
                "datosRecibidos", body,
                "timestamp", System.currentTimeMillis()
        );
    }
}
