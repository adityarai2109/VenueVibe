package com.adityarai.VenueVibe;

import com.adityarai.VenueVibe.model.Booking;
import com.adityarai.VenueVibe.model.Event;
import com.adityarai.VenueVibe.model.EventType;
import com.adityarai.VenueVibe.model.Venue;
import com.adityarai.VenueVibe.service.BookingService;
import com.adityarai.VenueVibe.service.EventService;
import com.adityarai.VenueVibe.service.VenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class VenueVibeApplication {
	private final VenueService venueService;
	private final EventService eventService;
	private final BookingService bookingService;

	public static void main(String[] args) {
		SpringApplication.run(VenueVibeApplication.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner() {
		return args -> {
			// 1. Admin adds venues
			log.info("1. Testing venue creation");
			Venue venue1 = venueService.addVenue("v_001", "SarjapurSocial", "Bengaluru", 200);
			Venue venue2 = venueService.addVenue("v_002", "ComedyClub", "Mumbai", 100);
			log.info("Venues added: {}, {}", venue1.getName(), venue2.getName());

			// Try to add duplicate venue
			Venue duplicateVenue = venueService.addVenue("v_001", "DuplicateVenue", "Delhi", 150);
			log.info("Duplicate venue creation attempt: {}", duplicateVenue);

			// 2. Admin adds events
			log.info("\n2. Testing event creation");

			log.info("\nTC123");

			Event event1 = eventService.addEvent(
				"e_001",
				"Comedy Night",
				"v_001",
				EventType.STANDUP,
				100,
				LocalDateTime.now().plusDays(1).withHour(19).withMinute(0),
				LocalDateTime.now().plusDays(1).withHour(21).withMinute(0),
				500.0
			);
			
			Event event2 = eventService.addEvent(
				"e_002",
				"Tech Conference",
				"v_001",
				EventType.CONFERENCE,
				80,
				LocalDateTime.now().plusDays(0).withHour(19).withMinute(30),
				LocalDateTime.now().plusDays(1).withHour(20).withMinute(30),
				1000.0
			);

			Event event3 = eventService.addEvent(
				"e_003",
				"Late Show",
				"v_001",
				EventType.STANDUP,
				50,
				LocalDateTime.now().plusDays(1).withHour(22).withMinute(0),
				LocalDateTime.now().plusDays(1).withHour(23).withMinute(30),
				300.0
			);
			log.info("Events added: {}, {}, {}", event1.getName(), event2.getName(), event3.getName());

			// Try to add event exceeding venue capacity
			Event overCapacityEvent = eventService.addEvent(
				"e_004",
				"Over Capacity Event",
				"v_001",
				EventType.CONFERENCE,
				50,
				LocalDateTime.now().plusDays(1).withHour(19).withMinute(0),
				LocalDateTime.now().plusDays(1).withHour(21).withMinute(0),
				200.0
			);
			log.info("Over capacity event creation attempt: {}", overCapacityEvent);

			log.info("\nTesting Customer Flows:");

			// 3. Search and view events
			log.info("3. Testing event search and view");
			List<Event> standupEvents = eventService.searchByEventType(EventType.STANDUP, EventService.SortType.PRICE_ASC);
			log.info("Standup events (sorted by price): {}", standupEvents);

			List<Event> venueEvents = eventService.getAllEvents("v_001");
			log.info("All events at venue v_001: {}", venueEvents);

			// 4. Test booking flow
			log.info("\n4. Testing booking flow");
			Booking booking1 = bookingService.bookEvent("c_001", "e_001");
			Booking booking2 = bookingService.bookEvent("c_002", "e_001");
			log.info("Bookings created: {}, {}", booking1.getBookingId(), booking2.getBookingId());

			// Try duplicate booking
			Booking duplicateBooking = bookingService.bookEvent("c_001", "e_001");
			log.info("Duplicate booking attempt: {}", duplicateBooking);

			// 5. Test waiting list
			log.info("\n5. Testing waiting list");
			// Book event until capacity
			for (int i = 3; i <= 100; i++) {
				bookingService.bookEvent("c_" + i, "e_001");
			}
			// These should go to waiting list
			bookingService.bookEvent("c_101", "e_001");
			bookingService.bookEvent("c_102", "e_001");
			log.info("Added customers to waiting list");

			// 6. View bookings
			log.info("\n6. Testing booking views");
			List<Booking> customerBookings = bookingService.getAllBookings("c_001");
			log.info("Bookings for customer c_001: {}", customerBookings);

			// 7. Test trending events
			log.info("\n7. Testing trending events");
			List<Event> trendingEvents = eventService.getTrendingEvents(2);
			log.info("Top 2 trending events: {}", trendingEvents);

			// 8. Test booking cancellation and waiting list processing
			log.info("\n8. Testing booking cancellation");
			boolean cancelled = bookingService.cancelBooking("c_001", booking1.getBookingId());
			log.info("Booking cancelled: {}, should process waiting list", cancelled);

			// 9. Test event removal
			log.info("\n9. Testing event removal\n\nTC456");
			boolean eventRemoved = eventService.removeEvent("e_002");
			Booking bookingAfterEventRemoved = bookingService.bookEvent("c_101","e_002");

			log.info("Event removed: {}", eventRemoved);

			// 10. Test cascade venue removal
			log.info("\n10. Testing venue cascade removal");
			
			// First create a new venue
			Venue venueToDelete = venueService.addVenue("v_003", "EventHub", "Delhi", 150);
			log.info("Created venue for deletion: {}", venueToDelete.getName());

			// Add events to this venue
			Event eventAtVenue = eventService.addEvent(
				"e_del_001",
				"Music Night",
				"v_003",
				EventType.STANDUP,
				50,
				LocalDateTime.now().plusDays(1).withHour(19).withMinute(0),
				LocalDateTime.now().plusDays(1).withHour(21).withMinute(0),
				300.0
			);
			log.info("Added event to venue: {}", eventAtVenue.getName());

			// Add some bookings
			Booking booking1AtVenue = bookingService.bookEvent("c_del_001", "e_del_001");
			Booking booking2AtVenue = bookingService.bookEvent("c_del_002", "e_del_001");
			log.info("Added bookings for event: {}, {}", booking1AtVenue.getBookingId(), booking2AtVenue.getBookingId());

			// Add someone to waiting list
			bookingService.bookEvent("c_del_wait", "e_del_001");
			log.info("Added customer to waiting list");

			// Now remove the venue - should cascade delete events, bookings and waiting list
			boolean venueDeleted = venueService.removeVenue("v_003");
			log.info("Venue removed with all events and bookings: {}", venueDeleted);

			// Verify event is gone
			List<Event> eventsAfterVenueRemoval = eventService.getAllEvents("v_003");
			log.info("Events at deleted venue: {}", eventsAfterVenueRemoval);

			// Verify bookings are gone
			List<Booking> bookingsAfterVenueRemoval = bookingService.getAllBookings("c_del_001");
			log.info("Bookings for customer after venue removal: {}", bookingsAfterVenueRemoval);



		};
	}
}
