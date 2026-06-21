package pl.flaaaxxx.eskatebackend.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import pl.flaaaxxx.eskatebackend.tables.pojos.UserLocations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static pl.flaaaxxx.eskatebackend.tables.UserLocations.USER_LOCATIONS;

@Repository
@RequiredArgsConstructor
public class UserLocationRepository {

    private final DSLContext dsl;


    /**
     * Pobiera tylko TYLKO JEDNĄ, najnowszą pozycję użytkownika
     * */
    public Optional<UserLocations> findByName(String name) {
        UserLocations latestLocation = dsl.selectFrom(USER_LOCATIONS)
                                          .where(USER_LOCATIONS.NAME.eq(name))
                                          .orderBy(USER_LOCATIONS.UPDATED_AT.desc()) // Najnowszy na górze
                                          .limit(1)                     // Bierzemy tylko jeden rekord
                                          .fetchOneInto(UserLocations.class);

        return Optional.ofNullable(latestLocation);
    }

    /**
     * Zapisuje pozycję użytkownika.
     * Jeśli 'name' już istnieje w bazie, zaktualizuje współrzędne i czas.
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
}