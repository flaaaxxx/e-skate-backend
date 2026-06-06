package pl.flaaaxxx.eskatebackend.model;

import lombok.Data;

import java.util.List;

@Data
public class FeatureCollection {
    private String type = "FeatureCollection";
    private List<RouteDto> features;

    public FeatureCollection(List<RouteDto> features) {
        this.features = features;
    }
}