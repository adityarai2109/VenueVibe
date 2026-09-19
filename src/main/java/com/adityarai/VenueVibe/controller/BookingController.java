package com.adityarai.VenueVibe.controller;

import com.adityarai.VenueVibe.model.Booking;
import com.adityarai.VenueVibe.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/create")
    public Booking bookEvent(@RequestParam String customerId,
                           @RequestParam String eventId) {
        return bookingService.bookEvent(customerId, eventId);
    }

    @GetMapping("/customer/{customerId}")
    public List<Booking> getAllBookings(@PathVariable String customerId) {
        return bookingService.getAllBookings(customerId);
    }

    @DeleteMapping("/{bookingId}/customer/{customerId}")
    public boolean cancelBooking(@PathVariable String customerId,
                               @PathVariable String bookingId) {
        return bookingService.cancelBooking(customerId, bookingId);
    }
} 