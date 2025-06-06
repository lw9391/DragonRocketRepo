package io.dragon.dataaccess;

import io.dragon.domain.Rocket;
import io.dragon.domain.RocketRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRocketRepository implements RocketRepository {

    private final Map<String, Rocket> rockets;

    public InMemoryRocketRepository() {
        this.rockets = new HashMap<>();
    }

    @Override
    public Rocket save(Rocket rocket) {
        if (rockets.containsKey(rocket.name()))
            throw new IllegalArgumentException("Rocket %s already exists".formatted(rocket.name()));
        rockets.put(rocket.name(), rocket);
        return rocket;
    }

    @Override
    public Rocket update(Rocket rocket) {
        if (!rockets.containsKey(rocket.name()))
            throw  new IllegalArgumentException(String.format("Rocket %s doesn't exist", rocket.name()));
        rockets.put(rocket.name(), rocket);
        return rocket;
    }

    public Optional<Rocket> findByName(String rocketName) {
        return Optional.ofNullable(rockets.get(rocketName));
    }


}
