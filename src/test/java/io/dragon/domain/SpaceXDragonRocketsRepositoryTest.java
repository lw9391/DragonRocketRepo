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
        assertThat(rocket.mission()).isEmpty();

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
}