package com.adityarai.VenueVibe.repository;

import com.adityarai.VenueVibe.model.Event;
import com.adityarai.VenueVibe.model.EventType;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EventRepository {
    private final Map<String, Event> events = new ConcurrentHashMap<>();

    public Event save(Event event) {
        events.put(event.getEventId(), event);
        return event;
    }

    public Event findById(String eventId) {
        return events.get(eventId);
    }

    public void delete(String eventId) {
        events.remove(eventId);
    }

    public List<Event> findByVenueId(String venueId) {
        return events.values().stream()
                .filter(event -> event.getVenueId().equals(venueId))
                .collect(Collectors.toList());
    }

    public List<Event> findByEventType(EventType eventType) {
        return events.values().stream()
                .filter(event -> event.getEventType().equals(eventType))
                .collect(Collectors.toList());
    }

    public List<Event> findAll() {
        return new ArrayList<>(events.values());
    }
} 