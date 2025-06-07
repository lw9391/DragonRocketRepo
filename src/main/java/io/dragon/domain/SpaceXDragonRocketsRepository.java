package io.dragon.domain;

import io.dragon.domain.exception.MissionAlreadyExistsException;
import io.dragon.domain.exception.RocketAlreadyExistsException;

public class SpaceXDragonRocketsRepository {

    private final RocketRepository rocketRepository;
    private final MissionRepository missionRepository;

    public SpaceXDragonRocketsRepository(RocketRepository rocketRepository, MissionRepository missionRepository) {
        this.rocketRepository = rocketRepository;
        this.missionRepository = missionRepository;
    }

    public Rocket addRocket(String rocketName) {
        if (rocketRepository.exists(rocketName))
            throw new RocketAlreadyExistsException(rocketName);
        Rocket rocket = Rocket.createNewRocket(rocketName);
        return rocketRepository.save(rocket);
    }

    public Mission addMission(String missionName) {
        if (missionRepository.exists(missionName))
            throw new MissionAlreadyExistsException(missionName);
        Mission mission = Mission.create(missionName);
        return missionRepository.save(mission);
    }
}
