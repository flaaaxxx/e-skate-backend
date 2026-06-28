package pl.flaaaxxx.eskatebackend.model;

import lombok.Builder;
import lombok.Data;
import pl.flaaaxxx.eskatebackend.tables.pojos.UserLocations;

import java.util.List;

@Data
@Builder
public class PagedUserLocationResponse {
    private List<UserLocations> data;
    private int currentPage;              // Aktualny numer strony
    private int pageSize;                 // Rozmiar strony
    private long totalElements;           // Łączna liczba tras w bazie
    private int totalPages;               // Łączna liczba stron
}

