package pl.flaaaxxx.eskatebackend.controllers;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jooq.Record;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteRepository routeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<String> saveRoute(@RequestBody RouteDto dto) {
        try {
            // 1. Konwertujemy tablicę współrzędnych na format WKT
            // Ważne: w WKT najpierw podajemy Longitude (Długość), potem Latitude (Szerokość)
            String wkt = "LINESTRING(" +
                    Arrays.stream(dto.getGeometry().getCoordinates())
                            .map(coord -> coord[0] + " " + coord[1])
                            .collect(Collectors.joining(", ")) +
                    ")";

            // 2. Wywołujemy zapis w repozytorium
            routeRepository.save(dto, wkt);

            return ResponseEntity.ok("Route saved successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoute(@PathVariable String id) {
        return ResponseEntity.ok(routeRepository.getFullRoute(id));
    }
}

// Dedykowana klasa wewnętrzna dla odpowiedzi
@Data
@AllArgsConstructor
class RouteResponse {
    private String type = "Feature";

    @JsonRawValue // KLUCZOWE: Mówi Jacksonowi: "to już jest JSON, nie dodawaj cudzysłowów"
    private String geometry;

    private Properties properties;

    @Data
    @AllArgsConstructor
    static class Properties {
        private String id;
        private String name;
        private String startDate;
        private Double totalDistance;
        private String unit;
    }
}