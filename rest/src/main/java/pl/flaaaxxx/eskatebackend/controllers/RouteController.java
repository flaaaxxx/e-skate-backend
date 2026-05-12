package pl.flaaaxxx.eskatebackend.controllers;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
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

    @GetMapping
    public ResponseEntity<?> getAllRoutes() {
        List<RouteResponse> features = routeRepository.getAllRoutes();
        return ResponseEntity.ok(new FeatureCollection(features));
    }
}

// Dedykowana klasa wewnętrzna dla odpowiedzi
@Data
@AllArgsConstructor
class RouteResponse {
    private String type = "Feature";
    private String id;

    @JsonRawValue // KLUCZOWE: Mówi Jacksonowi: "to już jest JSON, nie dodawaj cudzysłowów"
    private String geometry;

    private Properties properties;

    @Data
    @AllArgsConstructor
    static class Properties {
        private String name;
        private String startDate;
        private Double totalDistance;
        private String unit;
    }
}

@Data
@AllArgsConstructor
class FeatureCollection {
    private String type = "FeatureCollection";
    private List<RouteResponse> features;

    public FeatureCollection(List<RouteResponse> features) {
        this.type = "FeatureCollection";
        this.features = features;
    }
}