package pl.flaaaxxx.eskatebackend.controllers;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import pl.flaaaxxx.eskatebackend.tables.Routes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.*;
import static pl.flaaaxxx.eskatebackend.Tables.ROUTES;

@Repository
@RequiredArgsConstructor
public class RouteRepository {

    private final DSLContext dsl;

    public List<RouteResponse> getAllRoutes() {
        return dsl.select(
                        ROUTES.ID,
                        ROUTES.NAME,
                        ROUTES.START_DATE,
                        ROUTES.TOTAL_DISTANCE,
                        ROUTES.UNIT,
                        DSL.field("ST_AsGeoJSON({0})", String.class, ROUTES.PATH).as("geometry"))
                .from(ROUTES)
                .fetch(record -> new RouteResponse(
                        "Feature",
                        record.get(DSL.field("geometry", String.class)),
                        new RouteResponse.Properties(
                                record.get(ROUTES.ID),
                                record.get(ROUTES.NAME),
                                record.get(ROUTES.START_DATE) != null ? record.get(ROUTES.START_DATE).toString() : null,
                                record.get(ROUTES.TOTAL_DISTANCE) != null ? record.get(ROUTES.TOTAL_DISTANCE).doubleValue() : null,
                                record.get(ROUTES.UNIT)
                        )
                ));
    }

    @Transactional
    public void save(RouteDto dto, String wktPath) {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 11);

        dsl.insertInto(table("routes"))
                .set(ROUTES.ID, id)
                .set(ROUTES.NAME, dto.getProperties().getName())
                .set(ROUTES.START_DATE, dto.getProperties().getDate())
                .set(ROUTES.TOTAL_DISTANCE, dto.getProperties().getDistance() != null
                        ? BigDecimal.valueOf(dto.getProperties().getDistance())
                        : null).set(ROUTES.UNIT, dto.getProperties().getUnit())
                // Konwersja Stringa WKT na typ GEOMETRY w MySQL
                .set(ROUTES.PATH, DSL.field("ST_GeomFromText({0}, 0)", ROUTES.PATH.getType(), wktPath))
                .execute();
    }
}