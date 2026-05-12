package pl.flaaaxxx.eskatebackend.controllers;

import jakarta.transaction.Transactional;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import static org.jooq.impl.DSL.*;

@Repository
public class RouteRepository {

    private final DSLContext dsl;

    public RouteRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Record getFullRoute(String id) {
        return dsl.select(
                        field("id"),
                        field("name"),
                        field("start_date"),
                        field("total_distance"),
                        field("unit"),
                        field("ST_AsGeoJSON(path)").as("geometry") // Mapujemy geometrię na alias
                )
                .from(table("routes"))
                .where(field("id").eq(id))
                .fetchOne();
    }

    @Transactional
    public void save(RouteDto dto, String wktPath) {
        dsl.insertInto(table("routes"))
                .set(field("id"), dto.getId())
                .set(field("name"), dto.getProperties().getName())
                .set(field("start_date"), dto.getProperties().getDate())
                .set(field("total_distance"), dto.getProperties().getDistance())
                .set(field("unit"), dto.getProperties().getUnit())
                // Konwersja Stringa WKT na typ GEOMETRY w MySQL
                .set(field("path"), DSL.field("ST_GeomFromText({0}, 4326)", String.class, wktPath))
                .execute();
    }
}