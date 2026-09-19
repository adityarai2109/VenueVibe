package com.adityarai.VenueVibe.repository;

import com.adityarai.VenueVibe.model.Venue;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class VenueRepository {
    private final Map<String, Venue> venues = new ConcurrentHashMap<>();

    public Venue save(Venue venue) {
        venues.put(venue.getVenueId(), venue);
        return venue;
    }

    public Venue findById(String venueId) {
        return venues.get(venueId);
    }

    public void delete(String venueId) {
        venues.remove(venueId);
    }
} 