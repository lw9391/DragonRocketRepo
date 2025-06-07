package io.dragon.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record Mission(String name, Map<String, Rocket> rockets, boolean isEnded) {

    public static Mission create(String name) {
        return new Mission(name, Collections.emptyMap(), false);
    }

    public Mission assignRocket(Rocket rocket) {
        if (rockets.containsKey(rocket.name()))
            throw new IllegalArgumentException("This rocket is already assigned");
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

    public Mission updateRocket(Rocket rocket) {
        if (isEnded) throw new IllegalStateException("Mission is already ended");
        if (!rockets.containsKey(rocket.name())) {
            throw new IllegalArgumentException("Rocket is not assigned to the mission");
        }
        if (rocket.missionName().isEmpty() || !rocket.missionName().get().equals(name))
            throw new IllegalArgumentException("Rocket is not assigned to the mission");
        HashMap<String, Rocket> rocketsUpdated = new HashMap<>(this.rockets);
        rocketsUpdated.put(rocket.name(), rocket);
        return new Mission(this.name, Map.copyOf(rocketsUpdated), this.isEnded);
    }

    public String printToStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" - ").append(status().printableName).append(" - Dragons: ").append(rockets.size())
                .append("\n");
        rockets.values().forEach(rocket -> sb.append("\t").append(rocket.printToStatus()).append("\n"));
        return sb.toString();
    }

    private boolean hasDamagedRocket() {
        return rockets.entrySet().stream()
                .anyMatch(entry -> entry.getValue().status() == RocketStatus.IN_REPAIR);
    }

}
