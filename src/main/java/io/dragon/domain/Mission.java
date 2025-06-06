package io.dragon.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Mission(String name, List<Rocket> rockets) {

    public static Mission create(String name) {
        return new Mission(name, Collections.emptyList());
    }

    public Mission assignRocket(Rocket rocket) {
        List<Rocket> updatedRockets = new ArrayList<>(this.rockets);
        updatedRockets.add(rocket);
        return new Mission(name, List.copyOf(updatedRockets));
    }

}
