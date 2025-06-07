package io.dragon.domain;

import java.util.*;

public record Mission(String name, Map<String, Rocket> rockets, boolean isEnded) {

    public static Mission create(String name) {
        return new Mission(name, Collections.emptyMap(), false);
    }

    public Mission assignRocket(Rocket rocket) {
        Map<String, Rocket> updatedRockets = new HashMap<>(this.rockets);
        updatedRockets.put(rocket.name(), rocket);
        return new Mission(name, Map.copyOf(updatedRockets), isEnded);
    }

    public Mission endMission() {
        if (isEnded) throw new IllegalStateException("Mission is already ended");
        return new Mission(name, Collections.emptyMap(), true);
    }

    public MissionStatus status() {
        if (isEnded) return MissionStatus.ENDED;
        else if (rockets.isEmpty()) return MissionStatus.SCHEDULED;
        else if (hasDamagedRocket()) return MissionStatus.PENDING;
        else return MissionStatus.IN_PROGRESS;
    }

    private boolean hasDamagedRocket() {
        return rockets.entrySet().stream()
                .anyMatch(entry -> entry.getValue().status() == RocketStatus.IN_REPAIR);
    }

}
