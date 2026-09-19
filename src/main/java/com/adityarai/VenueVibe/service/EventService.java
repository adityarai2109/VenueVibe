package com.adityarai.VenueVibe.service;

import com.adityarai.VenueVibe.model.*;
import com.adityarai.VenueVibe.repository.EventRepository;
import com.adityarai.VenueVibe.repository.VenueRepository;
import com.adityarai.VenueVibe.repository.BookingRepository;
import com.adityarai.VenueVibe.repository.WaitingListRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final WaitingListRepository waitingListRepository;

    public Event addEvent(String eventId, String name, String venueId, EventType eventType, 
                         int maxParticipants, LocalDateTime startTime, LocalDateTime endTime, 
                         double ticketPrice) {
        log.info("Adding event: {} at venue: {}", name, venueId);
        
        // Validate venue and check concurrent event capacity
        Venue venue = venueRepository.findById(venueId);
        if (Objects.isNull(venue)) {
            log.error("Venue not found: {}", venueId);
            return null;
        }

        List<Event> overlappingEvents = eventRepository.findByVenueId(venueId).stream()
                .filter(e -> isTimeOverlapping(e.getStartTime(), e.getEndTime(), startTime, endTime))
                .collect(Collectors.toList());

        int totalParticipants = overlappingEvents.stream()
                .mapToInt(Event::getMaxParticipants)
                .sum();

        if (totalParticipants + maxParticipants > venue.getMaxCapacity()) {
            log.error("Venue capacity would be exceeded. Available: {}, Requested: {}, Booked: {}", 
                    venue.getMaxCapacity(), maxParticipants, totalParticipants);
            return null;
        }

        Event event = Event.builder()
                .eventId(eventId)
                .name(name)
                .venueId(venueId)
                .eventType(eventType)
                .maxParticipants(maxParticipants)
                .startTime(startTime)
                .endTime(endTime)
                .ticketPrice(ticketPrice)
                .build();

        return eventRepository.save(event);
    }

    private boolean isTimeOverlapping(LocalDateTime start1, LocalDateTime end1, 
                                    LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }

    public enum SortType {
        TIME_ASC,
        TIME_DESC,
        PRICE_ASC,
        PRICE_DESC
    }

    public List<Event> searchByEventType(EventType eventType, SortType sortType) {
        List<Event> events = eventRepository.findByEventType(eventType);
        
        switch (sortType) {
            case TIME_ASC:
                return events.stream()
                        .sorted(Comparator.comparing(Event::getStartTime))
                        .collect(Collectors.toList());
            case TIME_DESC:
                return events.stream()
                        .sorted(Comparator.comparing(Event::getStartTime).reversed())
                        .collect(Collectors.toList());
            case PRICE_ASC:
                return events.stream()
                        .sorted(Comparator.comparing(Event::getTicketPrice))
                        .collect(Collectors.toList());
            case PRICE_DESC:
                return events.stream()
                        .sorted(Comparator.comparing(Event::getTicketPrice).reversed())
                        .collect(Collectors.toList());
            default:
                return events;
        }
    }

    public List<Event> getAllEvents(String venueId) {
        return eventRepository.findByVenueId(venueId);
    }

    public boolean removeEvent(String eventId) {
        log.info("Removing event: {}", eventId);
        Event event = eventRepository.findById(eventId);
        if (Objects.isNull(event)) {
            log.error("Event not found: {}", eventId);
            return false;
        }

        // Remove all bookings for this event first
        List<Booking> eventBookings = bookingRepository.findByEventId(eventId);
        eventBookings.forEach(booking -> bookingRepository.delete(booking.getBookingId()));

        // Remove all waiting list entries for this event
        List<WaitingList> waitingList = waitingListRepository.findByEventId(eventId);
        waitingList.forEach(entry -> waitingListRepository.delete(entry.getWaitingId()));
        
        eventRepository.delete(eventId);
        log.info("Successfully removed event {} with {} bookings and {} waiting entries", 
                 eventId, eventBookings.size(), waitingList.size());
        return true;
    }

    public List<Event> getTrendingEvents(int topN) {
        log.info("Getting top {} trending events", topN);
        
        // Calculate and sort events by booking percentage
        return eventRepository.findAll().stream()
            .map(event -> {
                int totalBookings = bookingRepository.findByEventId(event.getEventId()).size();
                double bookingPercentage = (double) totalBookings / event.getMaxParticipants() * 100;
                return new EventWithBookingPercentage(event, bookingPercentage);
            })
            .sorted(Comparator.comparingDouble(EventWithBookingPercentage::getBookingPercentage).reversed())
            .limit(topN)
            .map(EventWithBookingPercentage::getEvent)
            .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    private static class EventWithBookingPercentage {
        private Event event;
        private double bookingPercentage;
    }
} 