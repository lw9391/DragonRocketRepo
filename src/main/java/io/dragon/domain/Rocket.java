package io.dragon.domain;

import java.util.Optional;

public record Rocket(String name, RocketStatus status, Optional<String> missionName) {

    public static Rocket createNewRocket(String name) {
        return new Rocket(name, RocketStatus.ON_GROUND, Optional.empty());
    }

    public Rocket inRepair() {
        return new Rocket(this.name, RocketStatus.IN_REPAIR, this.missionName);
    }

    public Rocket repaired() {
        RocketStatus rocketStatus = missionName.isEmpty()
                ? RocketStatus.ON_GROUND
                : RocketStatus.IN_SPACE;
        return new Rocket(this.name, rocketStatus, this.missionName);
    }

    public Rocket assignMission(String missionName) {
        if (this.missionName.isPresent()) throw new IllegalStateException("Mission already assigned");
        RocketStatus newStatus = this.status == RocketStatus.IN_REPAIR
                ? RocketStatus.IN_REPAIR : RocketStatus.IN_SPACE;
        return new Rocket(this.name, newStatus, Optional.of(missionName));
    }

    public Rocket removeMission() {
        if (this.missionName.isEmpty()) throw new IllegalStateException("Mission is not assigned");
        RocketStatus newStatus = this.status == RocketStatus.IN_REPAIR
                ? RocketStatus.IN_REPAIR : RocketStatus.ON_GROUND;
        return new Rocket(name, newStatus, Optional.empty());
    }

}
