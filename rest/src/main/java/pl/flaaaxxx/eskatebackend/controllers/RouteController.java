package pl.flaaaxxx.eskatebackend.controllers;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteRepository routeRepository;

    @PostMapping
    public ResponseEntity<ApiResponse> saveRoute(@RequestBody RouteDto dto) {
        try {
            // 1. Sprawdzenie struktury (załóżmy, że coordinates to double[][][] dla MULTILINESTRING)
            double[][][] segments = dto.getGeometry().getCoordinates();
            if (segments == null || segments.length == 0) {
                return ResponseEntity.badRequest().body(new ApiResponse("Error: Route must contain segments"));
            }

            // 2. Budowanie formatu MULTILINESTRING((X Y, X Y), (X Y, X Y))
            String wkt = "MULTILINESTRING(" +
                    Arrays.stream(segments)
                            .map(segment -> "(" +
                                    Arrays.stream(segment)
                                            .map(coord -> String.format(Locale.US, "%f %f", coord[0], coord[1]))
                                            .collect(Collectors.joining(", "))
                                    + ")")
                            .collect(Collectors.joining(", ")) +
                    ")";

            // 3. Wywołujemy zapis w repozytorium
            routeRepository.save(dto, wkt);

            // Sukces: Zwracamy obiekt ApiResponse
            return ResponseEntity.ok(new ApiResponse("Route saved successfully"));
        } catch (Exception e) {
            // Błąd: Tutaj TEŻ musimy zwrócić obiekt ApiResponse, aby typy się zgadzały!
            return ResponseEntity.internalServerError().body(new ApiResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRoutes() {
        System.out.println("getAllRoutes called");
        return ResponseEntity.ok(
                new FeatureCollection(routeRepository.getAllRoutes())
        );
    }
}

@Data
@AllArgsConstructor
class FeatureCollection {
    private String type = "FeatureCollection";
    private List<RouteDto> features;

    public FeatureCollection(List<RouteDto> features) {
        this.features = features;
    }
}
