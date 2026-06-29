package pl.flaaaxxx.eskatebackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.flaaaxxx.eskatebackend.model.PagedUserLocationResponse;
import pl.flaaaxxx.eskatebackend.repositories.UserLocationRepository;
import pl.flaaaxxx.eskatebackend.tables.pojos.UserLocations;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user-locations")
@RequiredArgsConstructor
public class UserLocationController {

    private final UserLocationRepository userLocationRepository;

    @GetMapping
    public ResponseEntity<PagedUserLocationResponse> getAll(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {

        var totalElements = userLocationRepository.count();
        var totalPages = (int) Math.ceil((double) totalElements / size);

        var response = PagedUserLocationResponse.builder()
                .data(userLocationRepository.getAllPaged(page, size))
                .currentPage(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();

        return ResponseEntity.ok(response);

    }

    /**
     * METODA DO WYSZUKIWANIA (GET)
     * Pobiera aktualną pozycję użytkownika na podstawie jego nazwy (wyszukiwanie po 'name')
     * Endpoint: GET /api/user-locations/{name}
     */
    @GetMapping("/get-locations")
    public ResponseEntity<List<UserLocations>> getLocationByNames(@RequestParam List<String> names) {
        return ResponseEntity.ok(userLocationRepository.findByNames(names));
    }

    /**
     * METODA DO ZAPISU / AKTUALIZACJI (POST)
     * Zapisuje nową pozycję lub aktualizuje istniejącą (UPSERT)
     * Endpoint: POST /api/user-locations
     */
    @PostMapping
    public ResponseEntity<Void> saveOrUpdateLocation(@RequestBody UserLocationRequest request) {
        userLocationRepository.save(
                request.name(),
                request.longitude(),
                request.latitude(),
                request.bearing(),
                request.battery()
        );
        return ResponseEntity.ok().build(); // Zwraca czyste HTTP 200 (Success)
    }
}

// Rekord dla żądania POST (Czego oczekujemy od Angulara/Capgo)
record UserLocationRequest(
        String name,
        BigDecimal longitude,
        BigDecimal latitude,
        BigDecimal bearing,
        Integer battery
) {}