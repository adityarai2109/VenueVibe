package com.adityarai.VenueVibe.service;

import com.adityarai.VenueVibe.model.Booking;
import com.adityarai.VenueVibe.model.Event;
import com.adityarai.VenueVibe.model.Venue;
import com.adityarai.VenueVibe.repository.VenueRepository;
import com.adityarai.VenueVibe.repository.EventRepository;
import com.adityarai.VenueVibe.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VenueService {
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public Venue addVenue(String venueId, String name, String location, int maxCapacity) {
        log.info("Adding venue: {} in {}", name, location);
        
        // Prevent duplicate venue IDs
        if (!Objects.isNull(venueRepository.findById(venueId))) {
            log.error("Venue with ID {} already exists", venueId);
            return null;
        }

        Venue venue = Venue.builder()
                .venueId(venueId)
                .name(name)
                .location(location)
                .maxCapacity(maxCapacity)
                .build();

        return venueRepository.save(venue);
    }

    public boolean removeVenue(String venueId) {
        log.info("Removing venue: {}", venueId);
        
        // Cascade delete venue and its events
        Venue venue = venueRepository.findById(venueId);
        if (Objects.isNull(venue)) {
            log.error("Venue not found: {}", venueId);
            return false;
        }

        // Get all events at this venue
        List<Event> venueEvents = eventRepository.findByVenueId(venueId);
        
        // Remove all bookings for each event
        venueEvents.forEach(event -> {
            List<Booking> eventBookings = bookingRepository.findByEventId(event.getEventId());
            eventBookings.forEach(booking -> bookingRepository.delete(booking.getBookingId()));
            eventRepository.delete(event.getEventId());
        });
        
        venueRepository.delete(venueId);
        log.info("Successfully removed venue {} with {} events", venueId, venueEvents.size());
        return true;
    }
} 