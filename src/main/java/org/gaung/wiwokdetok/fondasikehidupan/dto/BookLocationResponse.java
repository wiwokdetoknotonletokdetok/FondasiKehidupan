package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookLocationResponse {

    private int id;

    private String locationName;

    private List<Double> coordinates;

    private double distanceMeters;

    public BookLocationResponse(int id, String locationName, double latitude, double longitude, double distance) {
        this.id = id;
        this.locationName = locationName;
        this.coordinates = List.of(latitude, longitude);
        this.distanceMeters = distance;
    }
}
