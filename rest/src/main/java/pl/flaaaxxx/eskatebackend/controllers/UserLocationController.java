package pl.flaaaxxx.eskatebackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.flaaaxxx.eskatebackend.repositories.UserLocationRepository;
import pl.flaaaxxx.eskatebackend.tables.pojos.UserLocations;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/user-locations")
@RequiredArgsConstructor
public class UserLocationController {

    private final UserLocationRepository userLocationRepository;

    /**
     * METODA DO WYSZUKIWANIA (GET)
     * Pobiera aktualną pozycję użytkownika na podstawie jego nazwy (wyszukiwanie po 'name')
     * Endpoint: GET /api/user-locations/{name}
     */
    @GetMapping("/{name}")
    public ResponseEntity<UserLocations> getLocationByName(@PathVariable String name) {
        return userLocationRepository.findByName(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * METODA DO ZAPISU / AKTUALIZACJI (POST)
     * Zapisuje nową pozycję lub aktualizuje istniejącą (UPSERT)
     * Endpoint: POST /api/user-locations
     */
//    @PostMapping
//    public ResponseEntity<Void> saveOrUpdateLocation(@RequestBody UserLocationRequest request) {
//        userLocationRepository.save(
//                request.name(),
//                request.longitude(),
//                request.latitude(),
//                request.bearing()
//        );
//        return ResponseEntity.ok().build(); // Zwraca czyste HTTP 200 (Success)
//    }

    @PostMapping
    public ResponseEntity<?> saveOrUpdateLocation(@RequestBody Map<String, Object> payload) {
        // 1. Capgo wysyła współrzędne jako liczby (Double) na głównym poziomie JSON-a
        BigDecimal lat = (BigDecimal) payload.get("latitude");
        BigDecimal lng = (BigDecimal) payload.get("longitude");

        // 2. Kąt obrotu (bearing) również jest na głównym poziomie
        BigDecimal bearing = payload.get("bearing") != null ? (BigDecimal) payload.get("bearing") : BigDecimal.ZERO;

        // 3. Nasz string 'eskate' ukryty jest w obiekcie 'metadata'
        Map<String, Object> metadata = (Map<String, Object>) payload.get("metadata");
        String name = (String) metadata.get("name");

        // 4. Logika zapisu danych do bazy danych
        userLocationRepository.save(name, lng, lat, bearing);
        return ResponseEntity.ok().build();
    }
}

// Rekord dla żądania POST (Czego oczekujemy od Angulara/Capgo)
record UserLocationRequest(
        String name,
        BigDecimal longitude,
        BigDecimal latitude,
        BigDecimal bearing
) {}