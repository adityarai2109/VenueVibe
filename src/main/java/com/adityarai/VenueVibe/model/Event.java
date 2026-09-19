package com.adityarai.VenueVibe.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Event {
    private String eventId;
    private String name;
    private String venueId;
    private EventType eventType;
    private int maxParticipants;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double ticketPrice;
} 