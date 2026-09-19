package com.adityarai.VenueVibe.repository;

import com.adityarai.VenueVibe.model.WaitingList;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class WaitingListRepository {
    private final Map<String, WaitingList> waitingList = new ConcurrentHashMap<>();

    public WaitingList save(WaitingList entry) {
        waitingList.put(entry.getWaitingId(), entry);
        return entry;
    }

    public void delete(String waitingId) {
        waitingList.remove(waitingId);
    }

    public List<WaitingList> findByEventId(String eventId) {
        return waitingList.values().stream()
                .filter(entry -> entry.getEventId().equals(eventId))
                .sorted((e1, e2) -> e1.getJoinedAt().compareTo(e2.getJoinedAt()))
                .collect(Collectors.toList());
    }
} 