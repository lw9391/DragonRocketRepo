package io.dragon.domain;

import java.util.Optional;

public record Rocket(String name, RocketStatus status, Optional<String> missionName) {

    public static Rocket createNewRocket(String name) {
        return new Rocket(name, RocketStatus.ON_GROUND, Optional.empty());
    }

    public Rocket setStatus(RocketStatus status) {
        if (status == RocketStatus.IN_SPACE && missionName().isEmpty()) {
            throw new IllegalStateException("Cannot send rocket to space without mission");
        }
        return new Rocket(this.name, status, this.missionName);
    }

    public Rocket assignMission(String missionName) {
        if (this.missionName.isPresent()) throw new IllegalStateException("Mission already assigned");
        return new Rocket(this.name, status, Optional.of(missionName));
    }

    public Rocket removeMission() {
        if (this.missionName.isEmpty()) throw new IllegalStateException("Mission is not assigned");
        RocketStatus newStatus = this.status == RocketStatus.IN_REPAIR
                ? RocketStatus.IN_REPAIR : RocketStatus.ON_GROUND;
        return new Rocket(name, newStatus, Optional.empty());
    }

    public String printToStatus() {
        return name + " - " + status.printableName;
    }

}
