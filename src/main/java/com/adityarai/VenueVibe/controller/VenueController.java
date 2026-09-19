package com.adityarai.VenueVibe.controller;

import com.adityarai.VenueVibe.model.Venue;
import com.adityarai.VenueVibe.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {
    private final VenueService venueService;

    @PostMapping("/create")
    public Venue addVenue(@RequestParam String venueId,
                         @RequestParam String name,
                         @RequestParam String location,
                         @RequestParam int maxCapacity) {
        return venueService.addVenue(venueId, name, location, maxCapacity);
    }

    @DeleteMapping("/{venueId}")
    public boolean removeVenue(@PathVariable String venueId) {
        return venueService.removeVenue(venueId);
    }
} 