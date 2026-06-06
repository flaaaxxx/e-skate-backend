package pl.flaaaxxx.eskatebackend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RouteDto {

    private String id;
    private String type = "Feature";
    private Properties properties;
    private Geometry geometry;

    @Data
    public static class Properties {
        private String name;
        private LocalDateTime startDateTrip;
        private LocalDateTime endDateTrip;
        private Double distance;
        private String unit;
    }

    @Data
    public static class Geometry {
        private String type;
        private double[][][] coordinates;
    }
}