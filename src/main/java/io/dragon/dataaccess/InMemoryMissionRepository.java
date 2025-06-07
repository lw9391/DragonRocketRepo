package io.dragon.dataaccess;

import io.dragon.domain.Mission;
import io.dragon.domain.MissionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryMissionRepository implements MissionRepository {

    private final Map<String, Mission> missions;

    public InMemoryMissionRepository() {
        this.missions = new HashMap<>();
    }

    @Override
    public Mission save(Mission mission) {
        if (missions.containsKey(mission.name()))
            throw new IllegalArgumentException("Mission %s already exists".formatted(mission.name()));
        missions.put(mission.name(), mission);
        return mission;
    }

    @Override
    public Mission update(Mission mission) {
        if (!missions.containsKey(mission.name()))
            throw  new IllegalArgumentException(String.format("Mission %s doesn't exist", mission.name()));
        missions.put(mission.name(), mission);
        return mission;
    }

    @Override
    public Optional<Mission> findByName(String id) {
        return Optional.ofNullable(missions.get(id));
    }

    @Override
    public boolean exists(String id) {
        return missions.containsKey(id);
    }
}
