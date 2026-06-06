package pl.flaaaxxx.eskatebackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.flaaaxxx.eskatebackend.model.ApiResponse;
import pl.flaaaxxx.eskatebackend.model.FeatureCollection;
import pl.flaaaxxx.eskatebackend.model.PagedRouteResponse;
import pl.flaaaxxx.eskatebackend.model.RouteDto;
import pl.flaaaxxx.eskatebackend.repositories.RouteRepository;

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
    public ResponseEntity<PagedRouteResponse> getRoutes(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {

        // 1. Pobieramy łączną liczbę elementów
        long totalElements = routeRepository.countRoutes();

        // 2. Pobieramy tylko te elementy, które należą do wnioskowanej strony
        List<RouteDto> pagedRoutes = routeRepository.getAllRoutesPaged(page, size);
        FeatureCollection featureCollection = new FeatureCollection(pagedRoutes);

        // 3. Obliczamy całkowitą liczbę stron
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // 4. Budujemy obiekt odpowiedzi z metadanymi
        PagedRouteResponse response = PagedRouteResponse.builder()
                                                        .data(featureCollection)
                                                        .currentPage(page)
                                                        .pageSize(size)
                                                        .totalElements(totalElements)
                                                        .totalPages(totalPages)
                                                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable String id) {
        boolean deleted = routeRepository.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


