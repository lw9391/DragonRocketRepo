package io.dragon;

import java.util.Optional;

public record Rocket(String name, RocketStatus status, Optional<Mission> mission) {

    public static Rocket createNewRocket(String name) {
        return new Rocket(name, RocketStatus.ON_GROUND, Optional.empty());
    }

}

