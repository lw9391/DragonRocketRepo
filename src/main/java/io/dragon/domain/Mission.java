package io.dragon.domain;

import java.util.Collections;
import java.util.List;

public record Mission(String name, List<Rocket> rockets) {

    public static Mission create(String name) {
        return new Mission(name, Collections.emptyList());
    }

}
