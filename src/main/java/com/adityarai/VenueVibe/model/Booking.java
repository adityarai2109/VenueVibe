package com.adityarai.VenueVibe.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Booking {
    private String bookingId;
    private String customerId;
    private String eventId;
} 