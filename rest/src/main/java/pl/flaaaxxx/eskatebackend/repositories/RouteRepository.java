package pl.flaaaxxx.eskatebackend.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import pl.flaaaxxx.eskatebackend.model.RouteDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.table;
import static pl.flaaaxxx.eskatebackend.Tables.ROUTES;

@Repository
@RequiredArgsConstructor
public class RouteRepository {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void save(RouteDto dto, String wktPath) {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 11);

        dsl.insertInto(table("routes"))
                .set(ROUTES.ID, id)
                .set(ROUTES.NAME, dto.getProperties().getName())
                .set(ROUTES.START_DATE_TRIP, dto.getProperties().getStartDateTrip())
                .set(ROUTES.END_DATE_TRIP, dto.getProperties().getEndDateTrip())
                .set(ROUTES.TOTAL_DISTANCE, dto.getProperties().getDistance() != null
                        ? BigDecimal.valueOf(dto.getProperties().getDistance())
                        : null).set(ROUTES.UNIT, dto.getProperties().getUnit())
                // Konwersja Stringa WKT na typ GEOMETRY w MySQL
                .set(ROUTES.PATH, DSL.field("ST_GeomFromText({0}, 0)", ROUTES.PATH.getType(), wktPath))
                .execute();
    }

    public boolean delete(String id) {
       var deletedRouteId = dsl.delete(ROUTES).where(ROUTES.ID.eq(id)).execute();
       return deletedRouteId > 0;
    }

    public long countRoutes() {
        return dsl.selectCount()
                  .from(ROUTES)
                  .fetchOptional(0, Long.class)
                  .orElse(0L);
    }

    public List<RouteDto> getAllRoutesPaged(int page, int size) {
        int offset = page * size; // Wyliczamy od którego rekordu zacząć pobieranie

        return dsl.select(
                        ROUTES.ID,
                        ROUTES.NAME,
                        ROUTES.START_DATE_TRIP,
                        ROUTES.END_DATE_TRIP,
                        ROUTES.TOTAL_DISTANCE,
                        ROUTES.UNIT,
                        DSL.field("ST_AsGeoJSON({0})", String.class, ROUTES.PATH).as("geometry"))
                .from(ROUTES)
                .limit(size)    // Ile rekordów pobrać
                .offset(offset) // Ile rekordów pominąć
                .fetch(record -> {
                    try {
                        String geometryJson = record.get("geometry", String.class);
                        RouteDto.Geometry geometry = objectMapper.readValue(geometryJson, RouteDto.Geometry.class);

                        RouteDto dto = new RouteDto();
                        dto.setId(record.get(ROUTES.ID));
                        dto.setGeometry(geometry);

                        RouteDto.Properties properties = new RouteDto.Properties();
                        properties.setName(record.get(ROUTES.NAME));
                        properties.setStartDateTrip(record.get(ROUTES.START_DATE_TRIP));
                        properties.setEndDateTrip(record.get(ROUTES.END_DATE_TRIP));
                        properties.setUnit(record.get(ROUTES.UNIT));
                        properties.setDistance(
                                record.get(ROUTES.TOTAL_DISTANCE) != null ? record.get(ROUTES.TOTAL_DISTANCE).doubleValue() : null
                        );

                        dto.setProperties(properties);
                        return dto;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}