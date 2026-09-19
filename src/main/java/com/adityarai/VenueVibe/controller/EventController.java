package com.adityarai.VenueVibe.controller;

import com.adityarai.VenueVibe.model.Event;
import com.adityarai.VenueVibe.model.EventType;
import com.adityarai.VenueVibe.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/create")
    public Event addEvent(@RequestParam String eventId,
                         @RequestParam String name,
                         @RequestParam String venueId,
                         @RequestParam EventType eventType,
                         @RequestParam int maxParticipants,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                         @RequestParam double ticketPrice) {
        return eventService.addEvent(eventId, name, venueId, eventType, maxParticipants, 
                                   startTime, endTime, ticketPrice);
    }

    @GetMapping("/search")
    public List<Event> searchByEventType(@RequestParam EventType eventType,
                                       @RequestParam(defaultValue = "TIME_ASC") EventService.SortType sortType) {
        return eventService.searchByEventType(eventType, sortType);
    }

    @GetMapping("/venue/{venueId}")
    public List<Event> getAllEvents(@PathVariable String venueId) {
        return eventService.getAllEvents(venueId);
    }

    @GetMapping("/trending")
    public List<Event> getTrendingEvents(@RequestParam(defaultValue = "5") int limit) {
        return eventService.getTrendingEvents(limit);
    }

    @DeleteMapping("/{eventId}")
    public boolean removeEvent(@PathVariable String eventId) {
        return eventService.removeEvent(eventId);
    }
} 