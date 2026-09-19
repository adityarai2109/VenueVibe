package com.adityarai.VenueVibe.repository;

import com.adityarai.VenueVibe.model.Booking;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class BookingRepository {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    public Booking save(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
        return booking;
    }

    public void delete(String bookingId) {
        bookings.remove(bookingId);
    }

    public List<Booking> findByCustomerId(String customerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Booking> findByEventId(String eventId) {
        return bookings.values().stream()
                .filter(booking -> booking.getEventId().equals(eventId))
                .collect(Collectors.toList());
    }
} 