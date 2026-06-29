package pl.flaaaxxx.eskatebackend.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import pl.flaaaxxx.eskatebackend.tables.pojos.UserLocations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static pl.flaaaxxx.eskatebackend.tables.UserLocations.USER_LOCATIONS;

@Repository
@RequiredArgsConstructor
public class UserLocationRepository {

    private final DSLContext dsl;

    /**
     * Pobiera tylko TYLKO JEDNĄ, najnowszą pozycję użytkownika
     * @param names lista nazw użytkowników
     * */
    public Optional<UserLocations> findByNames(List<String> names) {
        UserLocations latestLocation = dsl.selectFrom(USER_LOCATIONS)
                                          .where(USER_LOCATIONS.NAME.in(names))
                                          .orderBy(USER_LOCATIONS.UPDATED_AT.desc()) // Najnowszy na górze
                                          .fetchOneInto(UserLocations.class);

        return Optional.ofNullable(latestLocation);
    }

    /**
     * Zapisuje pozycję użytkownika.
     * Jeśli 'name' już istnieje w bazie, zaktualizuje współrzędne i czas.
     * @param name
     * @param longitude
     * @param latitude
     * @param bearing
     * @param battery
     */
    public void save(String name, BigDecimal longitude, BigDecimal latitude, BigDecimal bearing, Integer battery) {
        dsl.insertInto(USER_LOCATIONS)
           .set(USER_LOCATIONS.NAME, name)
           .set(USER_LOCATIONS.LONGITUDE, longitude)
           .set(USER_LOCATIONS.LATITUDE, latitude)
           .set(USER_LOCATIONS.BEARING, bearing)
           .set(USER_LOCATIONS.BATTERY, battery)
           .set(USER_LOCATIONS.UPDATED_AT, LocalDateTime.now(ZoneId.of("Europe/Warsaw")))
           .onConflict(USER_LOCATIONS.NAME) // Jeśli wystąpi konflikt na kolumnie 'name'
           .doUpdate()                      // ...to zrób UPDATE zamiast INSERT
           .set(USER_LOCATIONS.LONGITUDE, longitude)
           .set(USER_LOCATIONS.LATITUDE, latitude)
           .set(USER_LOCATIONS.BEARING, bearing)
           .set(USER_LOCATIONS.BATTERY, battery)
           .set(USER_LOCATIONS.UPDATED_AT, LocalDateTime.now(ZoneId.of("Europe/Warsaw")))
           .execute();
    }

    public long count() {
        return dsl.selectCount()
                  .from(USER_LOCATIONS)
                  .fetchOptional(0, Long.class)
                  .orElse(0L);
    }

    public List<UserLocations> getAllPaged(int page, int size) {
        int offset = page * size; // Wyliczamy od którego rekordu zacząć pobieranie

        return dsl.selectFrom(USER_LOCATIONS)
                  .limit(size)    // Ile rekordów pobrać
                  .offset(offset) // Ile rekordów pominąć
                  .fetchInto(UserLocations.class);
    }
}