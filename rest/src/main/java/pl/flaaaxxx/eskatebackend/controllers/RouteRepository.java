package pl.flaaaxxx.eskatebackend.controllers;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import pl.flaaaxxx.eskatebackend.tables.Routes;

import java.math.BigDecimal;

import static org.jooq.impl.DSL.*;
import static pl.flaaaxxx.eskatebackend.Tables.ROUTES;

@Repository
@RequiredArgsConstructor
public class RouteRepository {

    private final DSLContext dsl;

    public RouteController.RouteResponse getFullRoute(String id) {
        return dsl.select(
                        ROUTES.ID,
                        ROUTES.NAME,
                        ROUTES.START_DATE,
                        ROUTES.TOTAL_DISTANCE,
                        ROUTES.UNIT,
                        DSL.field("ST_AsGeoJSON({0})", String.class, ROUTES.PATH).as("geometry"))
                .from(ROUTES)
                .where(ROUTES.ID.eq(id))
                .fetchOneInto(RouteController.RouteResponse.class);
    }

    @Transactional
    public void save(RouteDto dto, String wktPath) {
        dsl.insertInto(table("routes"))
                .set(ROUTES.ID, dto.getId())
                .set(ROUTES.NAME, dto.getProperties().getName())
                .set(ROUTES.START_DATE, dto.getProperties().getDate())
                .set(ROUTES.TOTAL_DISTANCE, dto.getProperties().getDistance() != null
                        ? BigDecimal.valueOf(dto.getProperties().getDistance())
                        : null)                .set(ROUTES.UNIT, dto.getProperties().getUnit())
                // Konwersja Stringa WKT na typ GEOMETRY w MySQL
                .set(ROUTES.PATH, DSL.field("ST_GeomFromText({0}, 4326)", ROUTES.PATH.getType(), wktPath))
                .execute();
    }
}