package io.dragon.domain;

import io.dragon.dataaccess.InMemoryMissionRepository;
import io.dragon.dataaccess.InMemoryRocketRepository;
import io.dragon.domain.exception.MissionAlreadyExistsException;
import io.dragon.domain.exception.RocketAlreadyExistsException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


class SpaceXDragonRocketsRepositoryTest {

    RocketRepository rocketRepository = new InMemoryRocketRepository();
    MissionRepository missionRepository = new InMemoryMissionRepository();
    SpaceXDragonRocketsRepository dragonRocketsRepository = new SpaceXDragonRocketsRepository(rocketRepository, missionRepository);

    @Test
    void shouldAddNewRocket() {
        //given new rocket name
        String rocketName = "snake-1";

        //when new rocket is added
        Rocket rocket = dragonRocketsRepository.addRocket(rocketName);

        //then rocket is created
        assertThat(rocket.name()).isEqualTo(rocketName);
        assertThat(rocket.status()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(rocket.missionName()).isEmpty();

        //and rocket is saved
        Optional<Rocket> saved = rocketRepository.findByName(rocketName);
        assertThat(saved).isNotEmpty();
        assertThat(saved).hasValue(rocket);
    }

    @Test
    void shouldThrowExceptionWhenTryingToCreateRocketWithExistingName() {
        //given new rocket name
        String rocketName = "stone-x";

        //when new rocket is added twice
        dragonRocketsRepository.addRocket(rocketName);

        //then exception is thrown
        assertThatThrownBy(() -> dragonRocketsRepository.addRocket(rocketName))
                .isExactlyInstanceOf(RocketAlreadyExistsException.class)
                .hasMessage("Rocket with name %s already exists in the system".formatted(rocketName));
    }

    @Test
    void shouldAddNewMission() {
        //given new mission name
        String missionName = "jupyter-1";

        //when new mission is added
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //then mission is created
        assertThat(mission.name()).isEqualTo(missionName);
        assertThat(mission.rockets()).isEmpty();

        //and mission is saved
        Optional<Mission> saved = missionRepository.findByName(missionName);
        assertThat(saved).isNotEmpty();
        assertThat(saved).hasValue(mission);
    }

    @Test
    void shouldThrowExceptionWhenTryingToCreateMissionWithExistingName() {
        //given new mission name
        String missionName = "stone-x";

        //when new mission is added twice
        dragonRocketsRepository.addMission(missionName);

        //then exception is thrown
        assertThatThrownBy(() -> dragonRocketsRepository.addMission(missionName))
                .isExactlyInstanceOf(MissionAlreadyExistsException.class)
                .hasMessage("Mission with name %s already exists in the system".formatted(missionName));
    }

    @Test
    void shouldAssignRocketToMission() {
        //given new mission
        String missionName = "axis-x";
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //and new rocket
        String rocketName = "fast-l";
        Rocket rocket = dragonRocketsRepository.addRocket(rocketName);

        //when rocket is assigned to mission
        AssigmentResult assigmentResult = dragonRocketsRepository.assignRocketToMission(rocket, mission);

        //then mission object is assign to rocket
        Optional<Rocket> optionalRocket = rocketRepository.findByName(rocketName);
        assertThat(optionalRocket).isNotEmpty();
        Rocket savedRocket = optionalRocket.get();
        assertThat(savedRocket.missionName()).hasValue(mission.name());

        //and rocket status is updated
        assertThat(savedRocket.status()).isEqualTo(RocketStatus.IN_SPACE);

        //and rocket is assigned to mission
        Optional<Mission> optionalMission = missionRepository.findByName(missionName);
        assertThat(optionalMission).isNotEmpty();
        Mission savedMission = optionalMission.get();
        assertThat(savedMission.rockets()).hasSize(1);
        assertThat(savedMission.rockets().get(0)).isEqualTo(savedRocket);

        //and returned objects matches db state
        assertThat(assigmentResult.mission()).isEqualTo(savedMission);
        assertThat(assigmentResult.rocket()).isEqualTo(savedRocket);
    }

    @Test
    void shouldThrowExceptionWhenAssigningRocketWhichAlreadyHasMission() {
        //given new mission
        String missionName = "axis-a";
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //and rocket with mission
        String rocketName = "fast-xl";
        Rocket rocket = dragonRocketsRepository.addRocket(rocketName).assignMission("existing");

        //when rocket is assigned to mission
        assertThatThrownBy(() -> dragonRocketsRepository.assignRocketToMission(rocket, mission))
                .isExactlyInstanceOf(IllegalStateException.class);
    }
}