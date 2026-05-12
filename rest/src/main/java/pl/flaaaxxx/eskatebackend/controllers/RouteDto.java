package pl.flaaaxxx.eskatebackend.controllers;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RouteDto {
    private String id;
    private Properties properties;
    private Geometry geometry;

    @Data
    public static class Properties {
        private String name;
        private LocalDateTime date;
        private Double distance;
        private String unit;
    }

    @Data
    public static class Geometry {
        private String type; // np. "LineString"
        private double[][] coordinates;
    }
}