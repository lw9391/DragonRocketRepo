package io.dragon.domain;

import io.dragon.dataaccess.InMemoryMissionRepository;
import io.dragon.dataaccess.InMemoryRocketRepository;
import io.dragon.domain.exception.MissionAlreadyExistsException;
import io.dragon.domain.exception.RocketAlreadyExistsException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

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

    @Test
    void shouldAssignMultipleRocketsToMission() {
        //given new mission
        String missionName = "axis-y";
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //and multiple new rockets
        String rocket1Name = "fast-m1";
        String rocket2Name = "fast-m2";
        String rocket3Name = "fast-m3";
        Rocket rocket1 = dragonRocketsRepository.addRocket(rocket1Name);
        Rocket rocket2 = dragonRocketsRepository.addRocket(rocket2Name);
        Rocket rocket3 = dragonRocketsRepository.addRocket(rocket3Name);
        Set<Rocket> rockets = Set.of(rocket1, rocket2, rocket3);

        //when rockets are assigned to mission
        GroupAssigmentResult groupAssigmentResult = dragonRocketsRepository.assignRocketsToMission(rockets, mission);

        //then all rockets have mission assigned
        Optional<Rocket> optionalRocket1 = rocketRepository.findByName(rocket1Name);
        Optional<Rocket> optionalRocket2 = rocketRepository.findByName(rocket2Name);
        Optional<Rocket> optionalRocket3 = rocketRepository.findByName(rocket3Name);

        assertThat(optionalRocket1).isNotEmpty();
        assertThat(optionalRocket2).isNotEmpty();
        assertThat(optionalRocket3).isNotEmpty();

        Rocket savedRocket1 = optionalRocket1.get();
        Rocket savedRocket2 = optionalRocket2.get();
        Rocket savedRocket3 = optionalRocket3.get();

        assertThat(savedRocket1.missionName()).hasValue(mission.name());
        assertThat(savedRocket2.missionName()).hasValue(mission.name());
        assertThat(savedRocket3.missionName()).hasValue(mission.name());

        //and all rockets status is updated
        assertThat(savedRocket1.status()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(savedRocket2.status()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(savedRocket3.status()).isEqualTo(RocketStatus.IN_SPACE);

        //and all rockets are assigned to mission
        Optional<Mission> optionalMission = missionRepository.findByName(missionName);
        assertThat(optionalMission).isNotEmpty();
        Mission savedMission = optionalMission.get();
        assertThat(savedMission.rockets()).hasSize(3);
        assertThat(savedMission.rockets()).containsExactlyInAnyOrder(savedRocket1, savedRocket2, savedRocket3);

        //and returned objects match db state
        assertThat(groupAssigmentResult.mission()).isEqualTo(savedMission);
        assertThat(groupAssigmentResult.rockets()).hasSize(3);
        assertThat(groupAssigmentResult.rockets()).containsExactlyInAnyOrder(savedRocket1, savedRocket2, savedRocket3);
    }

    @Test
    void shouldAssignEmptySetOfRocketsToMission() {
        //given new mission
        String missionName = "axis-z";
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //and empty set of rockets
        Set<Rocket> rockets = Set.of();

        //when empty set of rockets is assigned to mission
        GroupAssigmentResult groupAssigmentResult = dragonRocketsRepository.assignRocketsToMission(rockets, mission);

        //then mission has no rockets
        Optional<Mission> optionalMission = missionRepository.findByName(missionName);
        assertThat(optionalMission).isNotEmpty();
        Mission savedMission = optionalMission.get();
        assertThat(savedMission.rockets()).hasSize(0);

        //and returned objects match db state
        assertThat(groupAssigmentResult.mission()).isEqualTo(savedMission);
        assertThat(groupAssigmentResult.rockets()).hasSize(0);
    }

    @Test
    void shouldThrowExceptionWhenAssigningRocketsWhereOneAlreadyHasMission() {
        //given new mission
        String missionName = "axis-b";
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //and rockets where one already has a mission
        String rocket1Name = "fast-n1";
        String rocket2Name = "fast-n2";
        Rocket rocket1 = dragonRocketsRepository.addRocket(rocket1Name);
        Rocket rocket2 = dragonRocketsRepository.addRocket(rocket2Name);
        Rocket rocket2WithMission = rocket2.assignMission("existing");
        Set<Rocket> rockets = Set.of(rocket1, rocket2WithMission);

        //when rockets are assigned to mission
        assertThatThrownBy(() -> dragonRocketsRepository.assignRocketsToMission(rockets, mission))
                .isExactlyInstanceOf(IllegalStateException.class);

        //and no updates were made to db
        assertThat(rocketRepository.findByName(rocket1Name)).hasValue(rocket1);
        assertThat(rocketRepository.findByName(rocket2Name)).hasValue(rocket2);
        assertThat(missionRepository.findByName(missionName)).hasValue(mission);
    }

    @Test
    void shouldThrowExceptionWhenAssigningRocketsWhereMultipleAlreadyHaveMissions() {
        //given new mission
        String missionName = "axis-c";
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //and rockets where multiple already have missions
        String rocket1Name = "fast-o1";
        String rocket2Name = "fast-o2";
        String rocket3Name = "fast-o3";
        Rocket rocket1 = dragonRocketsRepository.addRocket(rocket1Name);
        Rocket rocket1WithMission = rocket1.assignMission("existing1");
        Rocket rocket2 = dragonRocketsRepository.addRocket(rocket2Name);
        Rocket rocket3 = dragonRocketsRepository.addRocket(rocket3Name);
        Rocket rocket3WithMission = rocket3.assignMission("existing2");
        Set<Rocket> rockets = Set.of(rocket1WithMission, rocket2, rocket3WithMission);

        //when rockets are assigned to mission
        assertThatThrownBy(() -> dragonRocketsRepository.assignRocketsToMission(rockets, mission))
                .isExactlyInstanceOf(IllegalStateException.class);

        //and no updates were made to db
        assertThat(rocketRepository.findByName(rocket1Name)).hasValue(rocket1);
        assertThat(rocketRepository.findByName(rocket2Name)).hasValue(rocket2);
        assertThat(rocketRepository.findByName(rocket3Name)).hasValue(rocket3);
        assertThat(missionRepository.findByName(missionName)).hasValue(mission);
    }

    @Test
    void shouldAssignSingleRocketToMissionUsingGroupMethod() {
        //given new mission
        String missionName = "axis-d";
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //and single rocket in a set
        String rocketName = "fast-single";
        Rocket rocket = dragonRocketsRepository.addRocket(rocketName);
        Set<Rocket> rockets = Set.of(rocket);

        //when single rocket in set is assigned to mission
        GroupAssigmentResult groupAssigmentResult = dragonRocketsRepository.assignRocketsToMission(rockets, mission);

        //then rocket has mission assigned
        Optional<Rocket> optionalRocket = rocketRepository.findByName(rocketName);
        assertThat(optionalRocket).isNotEmpty();
        Rocket savedRocket = optionalRocket.get();
        assertThat(savedRocket.missionName()).hasValue(mission.name());
        assertThat(savedRocket.status()).isEqualTo(RocketStatus.IN_SPACE);

        //and rocket is assigned to mission
        Optional<Mission> optionalMission = missionRepository.findByName(missionName);
        assertThat(optionalMission).isNotEmpty();
        Mission savedMission = optionalMission.get();
        assertThat(savedMission.rockets()).hasSize(1);
        assertThat(savedMission.rockets().get(0)).isEqualTo(savedRocket);

        //and returned objects match db state
        assertThat(groupAssigmentResult.mission()).isEqualTo(savedMission);
        assertThat(groupAssigmentResult.rockets()).hasSize(1);
        assertThat(groupAssigmentResult.rockets()).contains(savedRocket);
    }
}