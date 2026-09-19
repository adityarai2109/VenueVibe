package com.adityarai.VenueVibe.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Venue {
    private String venueId;
    private String name;
    private String location;
    private int maxCapacity;
} 