package com.adityarai.VenueVibe.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WaitingList {
    private String waitingId;
    private String eventId;
    private String customerId;
    private LocalDateTime joinedAt;
} 