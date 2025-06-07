package io.dragon.domain;

import java.util.Collection;
import java.util.Optional;

public interface MissionRepository {

    Mission save(Mission mission);

    Mission update(Mission mission);

    Optional<Mission> findByName(String id);

    boolean exists(String id);

    Collection<Mission> findAll();

}
