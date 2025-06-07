package io.dragon.domain;

import io.dragon.domain.exception.MissionAlreadyExistsException;
import io.dragon.domain.exception.RocketAlreadyExistsException;

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

    public AssigmentResult assignRocketToMission(Rocket rocket, Mission mission) {
        Rocket withMission = rocket.assignMission(mission.name());
        Mission withRocket = mission.assignRocket(withMission);
        rocketRepository.update(withMission);
        missionRepository.update(withRocket);
        return new AssigmentResult(withRocket, withMission);
    }

    public GroupAssigmentResult assignRocketsToMission(Set<Rocket> rockets, Mission mission) {
        List<Rocket> rocketsWithMission = rockets.stream()
                .map(rocket -> rocket.assignMission(mission.name()))
                .toList();
        Mission withRockets = mission;
        for (Rocket withMission : rocketsWithMission) {
            withRockets = withRockets.assignRocket(withMission);
        }
        rocketsWithMission.forEach(rocketRepository::update);
        missionRepository.update(withRockets);
        return new GroupAssigmentResult(withRockets, rocketsWithMission);
    }

    public void setRocketAsDamaged(String rocketName) {
        Rocket rocket = rocketRepository.findByName(rocketName)
                .orElseThrow(() -> new IllegalStateException("Rocket does not exists"));
        if (rocket.status() == RocketStatus.IN_REPAIR) return;
        Rocket updatedRocket = rocket.inRepair();
        updateRocket(updatedRocket);
    }

    public void setRocketAsRepaired(String rocketName) {
        Rocket rocket = rocketRepository.findByName(rocketName)
                .orElseThrow(() -> new IllegalStateException("Rocket does not exists"));
        if (rocket.status() != RocketStatus.IN_REPAIR) return;
        Rocket updatedRocket = rocket.repaired();
        updateRocket(updatedRocket);
    }

    public void endMission(String missionName) {
        Mission mission = missionRepository.findByName(missionName)
                .orElseThrow(() -> new IllegalStateException("Mission does not exist"));
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
            Mission mission = missionRepository.findByName(missionName.get())
                    .orElseThrow(() -> new IllegalStateException("Mission does not exist"));
            Mission updatedMission = mission.updateRocket(updatedRocket);
            missionRepository.update(updatedMission);
        }
    }


}
