package io.dragon.domain;

import java.util.Optional;

public interface RocketRepository {

    Rocket save(Rocket rocket);

    Rocket update(Rocket rocket);

    Optional<Rocket> findByName(String id);

    boolean exists(String id);

}
