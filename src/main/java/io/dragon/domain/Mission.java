package io.dragon.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Mission(String name, List<Rocket> rockets, boolean isEnded) {

    public static Mission create(String name) {
        return new Mission(name, Collections.emptyList(), false);
    }

    public Mission assignRocket(Rocket rocket) {
        List<Rocket> updatedRockets = new ArrayList<>(this.rockets);
        updatedRockets.add(rocket);
        return new Mission(name, List.copyOf(updatedRockets), isEnded);
    }

    public Mission endMission() {
        if (isEnded) throw new IllegalStateException("Mission is already ended");
        return new Mission(name, Collections.emptyList(), true);
    }

    public MissionStatus status() {
        if (isEnded) return MissionStatus.ENDED;
        else if (rockets.isEmpty()) return MissionStatus.SCHEDULED;
        else if (hasDamagedRocket()) return MissionStatus.PENDING;
        else return MissionStatus.IN_PROGRESS;

    }

    private boolean hasDamagedRocket() {
        return rockets.stream()
                .anyMatch(rocket -> rocket.status() == RocketStatus.IN_REPAIR);
    }

}
