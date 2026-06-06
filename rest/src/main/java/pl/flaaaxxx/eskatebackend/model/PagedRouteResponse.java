package pl.flaaaxxx.eskatebackend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagedRouteResponse {
    private FeatureCollection data;
    private int currentPage;              // Aktualny numer strony
    private int pageSize;                 // Rozmiar strony
    private long totalElements;           // Łączna liczba tras w bazie
    private int totalPages;               // Łączna liczba stron
}

