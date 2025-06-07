package io.dragon.domain;

import io.dragon.domain.exception.MissionAlreadyExistsException;
import io.dragon.domain.exception.MissionDoesNotExistException;
import io.dragon.domain.exception.RocketAlreadyExistsException;
import io.dragon.domain.exception.RocketDoesNotExistException;

import java.util.*;

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

    public void assignRocketToMission(String rocketName, String missionName) {
        Rocket rocket = rocketRepository.findByName(rocketName)
                .orElseThrow(() -> new RocketDoesNotExistException(rocketName));
        Mission mission = missionRepository.findByName(missionName)
                .orElseThrow(() -> new MissionDoesNotExistException(missionName));
        Rocket withMission = rocket.assignMission(mission.name());
        Mission withRocket = mission.assignRocket(withMission);
        rocketRepository.update(withMission);
        missionRepository.update(withRocket);
    }

    public void assignRocketsToMission(Set<String> rocketsNames, String missionName) {
        List<Rocket> rocketsWithMission = rocketsNames.stream()
                .map(name -> rocketRepository.findByName(name).orElseThrow(() -> new RocketDoesNotExistException(name)))
                .map(rocket -> rocket.assignMission(missionName))
                .toList();
        Mission withRockets = missionRepository.findByName(missionName)
                .orElseThrow(() -> new MissionDoesNotExistException(missionName));
        for (Rocket withMission : rocketsWithMission) {
            withRockets = withRockets.assignRocket(withMission);
        }
        rocketsWithMission.forEach(rocketRepository::update);
        missionRepository.update(withRockets);
    }

    public void setRocketAsDamaged(String rocketName) {
        Rocket rocket = rocketRepository.findByName(rocketName)
                .orElseThrow(() -> new RocketDoesNotExistException(rocketName));
        if (rocket.status() == RocketStatus.IN_REPAIR) return;
        Rocket updatedRocket = rocket.inRepair();
        updateRocket(updatedRocket);
    }

    public void setRocketAsRepaired(String rocketName) {
        Rocket rocket = rocketRepository.findByName(rocketName)
                .orElseThrow(() -> new RocketDoesNotExistException(rocketName));
        if (rocket.status() != RocketStatus.IN_REPAIR) return;
        Rocket updatedRocket = rocket.repaired();
        updateRocket(updatedRocket);
    }

    public void endMission(String missionName) {
        Mission mission = missionRepository.findByName(missionName)
                .orElseThrow(() -> new MissionDoesNotExistException(missionName));
        mission.rockets().values().stream()
                .map(Rocket::removeMission)
                .forEach(rocketRepository::update);
        Mission ended = mission.endMission();
        missionRepository.update(ended);
    }

    public Summary getDragonsSummary() {
        return new Summary(missionRepository.findAll()
                .stream()
                .sorted(Comparator
                        .comparingInt((Mission mission) -> mission.rockets().size())
                        .reversed()
                        .thenComparing(Mission::name, Comparator.reverseOrder()))
                .toList());
    }

    private void updateRocket(Rocket updatedRocket) {
        rocketRepository.update(updatedRocket);
        Optional<String> missionName = updatedRocket.missionName();
        if (missionName.isPresent()) {
            String name = missionName.get();
            Mission mission = missionRepository.findByName(name)
                    .orElseThrow(() -> new MissionDoesNotExistException(name));
            Mission updatedMission = mission.updateRocket(updatedRocket);
            missionRepository.update(updatedMission);
        }
    }

}
