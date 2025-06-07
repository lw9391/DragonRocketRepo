package io.dragon.domain;

import io.dragon.domain.exception.RocketAlreadyExistsException;

public class SpaceXDragonRocketsRepository {

    private final RocketRepository rocketRepository;

    public SpaceXDragonRocketsRepository(RocketRepository rocketRepository) {
        this.rocketRepository = rocketRepository;
    }

    public Rocket addRocket(String rocketName) {
        if (rocketRepository.exists(rocketName))
            throw new RocketAlreadyExistsException(rocketName);
        Rocket rocket = Rocket.createNewRocket(rocketName);
        return rocketRepository.save(rocket);
    }
}
