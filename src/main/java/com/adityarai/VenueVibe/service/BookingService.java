package com.adityarai.VenueVibe.service;

import com.adityarai.VenueVibe.model.Booking;
import com.adityarai.VenueVibe.model.Event;
import com.adityarai.VenueVibe.model.WaitingList;
import com.adityarai.VenueVibe.repository.BookingRepository;
import com.adityarai.VenueVibe.repository.EventRepository;
import com.adityarai.VenueVibe.repository.WaitingListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final WaitingListRepository waitingListRepository;

    public Booking bookEvent(String customerId, String eventId) {
        log.info("Booking event: {} for customer: {}", eventId, customerId);
        
        // Validate event existence and timing
        Event event = eventRepository.findById(eventId);
        if (Objects.isNull(event)) {
            log.error("Event not found: {}", eventId);
            return null;
        }

        if (event.getStartTime().isBefore(LocalDateTime.now())) {
            log.error("Cannot book past event: {}", eventId);
            return null;
        }

        // Check duplicate booking and capacity
        List<Booking> existingBookings = bookingRepository.findByEventId(eventId);
        if (existingBookings.stream().anyMatch(booking -> booking.getCustomerId().equals(customerId))) {
            log.error("Customer {} already has a booking for event {}", customerId, eventId);
            return null;
        }

        if (existingBookings.size() >= event.getMaxParticipants()) {
            // Add to waiting list if event is full
            WaitingList waitingEntry = WaitingList.builder()
                    .waitingId(UUID.randomUUID().toString())
                    .eventId(eventId)
                    .customerId(customerId)
                    .joinedAt(LocalDateTime.now())
                    .build();
            waitingListRepository.save(waitingEntry);
            log.info("Event full, added customer {} to waiting list", customerId);
            return null;
        }

        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID().toString())
                .customerId(customerId)
                .eventId(eventId)
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings(String customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public boolean cancelBooking(String customerId, String bookingId) {
        log.info("Canceling booking: {} for customer: {}", bookingId, customerId);
        
        // Validate booking ownership and event timing
        Booking bookingToCancel = bookingRepository.findByCustomerId(customerId).stream()
                .filter(booking -> booking.getBookingId().equals(bookingId))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(bookingToCancel)) {
            log.error("Booking not found or not authorized: {}", bookingId);
            return false;
        }

        Event event = eventRepository.findById(bookingToCancel.getEventId());
        if (Objects.isNull(event) || event.getStartTime().isBefore(LocalDateTime.now())) {
            log.error("Cannot cancel: event not found or already started");
            return false;
        }

        bookingRepository.delete(bookingId);
        log.info("Successfully cancelled booking {} for event {}", bookingId, event.getEventId());

        // Process waiting list
        List<WaitingList> waitingList = waitingListRepository.findByEventId(event.getEventId());
        if (!waitingList.isEmpty()) {
            WaitingList nextInLine = waitingList.get(0);
            
            Booking newBooking = Booking.builder()
                    .bookingId(UUID.randomUUID().toString())
                    .customerId(nextInLine.getCustomerId())
                    .eventId(event.getEventId())
                    .build();
            
            newBooking = bookingRepository.save(newBooking);
            if (newBooking != null) {
                waitingListRepository.delete(nextInLine.getWaitingId());
                log.info("Successfully booked customer {} from waiting list", nextInLine.getCustomerId());
            } else {
                log.error("Failed to book customer {} from waiting list", nextInLine.getCustomerId());
            }
        }

        return true;
    }

    public int getCurrentBookingsCount(String eventId) {
        return bookingRepository.findByEventId(eventId).size();
    }
} 