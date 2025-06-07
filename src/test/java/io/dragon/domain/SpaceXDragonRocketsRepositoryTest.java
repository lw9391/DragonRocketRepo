package io.dragon.domain;

import io.dragon.dataaccess.InMemoryMissionRepository;
import io.dragon.dataaccess.InMemoryRocketRepository;
import io.dragon.domain.exception.MissionAlreadyExistsException;
import io.dragon.domain.exception.MissionDoesNotExistException;
import io.dragon.domain.exception.RocketAlreadyExistsException;
import io.dragon.domain.exception.RocketDoesNotExistException;
import org.junit.jupiter.api.Test;

import java.util.List;
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
        dragonRocketsRepository.addRocket(rocketName);

        //when rocket is assigned to mission
        dragonRocketsRepository.assignRocketToMission(rocketName, missionName);

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
        assertThat(savedMission.rockets().get(rocketName)).isEqualTo(savedRocket);
    }

    @Test
    void shouldThrowExceptionWhenAssigningNonExistingRocketToMission() {
        //given new mission
        String missionName = "axis-x";
        dragonRocketsRepository.addMission(missionName);

        //and non-existing rocket
        String rocketName = "fast-l";

        //when rocket is assigned to mission exception is thrown
        assertThatThrownBy(() -> dragonRocketsRepository.assignRocketToMission(rocketName, missionName))
                .isExactlyInstanceOf(RocketDoesNotExistException.class);
    }

    @Test
    void shouldThrowExceptionWhenAssigningRocketToNonExistingMission() {
        //given non-existing mission
        String missionName = "axis-x";

        //and rocket
        String rocketName = "fast-l";
        dragonRocketsRepository.addRocket(rocketName);

        //when rocket is assigned to mission exception is thrown
        assertThatThrownBy(() -> dragonRocketsRepository.assignRocketToMission(rocketName, missionName))
                .isExactlyInstanceOf(MissionDoesNotExistException.class);
    }

    @Test
    void shouldThrowExceptionWhenAssigningRocketWhichAlreadyHasMission() {
        //given new mission
        String missionName = "axis-a";
        String anotherMission = "axis-b";
        dragonRocketsRepository.addMission(missionName);
        dragonRocketsRepository.addMission(anotherMission);

        //and rocket with mission
        String rocketName = "fast-xl";
        dragonRocketsRepository.addRocket(rocketName);
        dragonRocketsRepository.assignRocketToMission(rocketName, missionName);

        //when rocket is assigned to another mission
        assertThatThrownBy(() -> dragonRocketsRepository.assignRocketToMission(rocketName, anotherMission))
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
        dragonRocketsRepository.addRocket(rocket1Name);
        dragonRocketsRepository.addRocket(rocket2Name);
        dragonRocketsRepository.addRocket(rocket3Name);
        Set<String> rockets = Set.of(rocket1Name, rocket2Name, rocket3Name);

        //when rockets are assigned to mission
        dragonRocketsRepository.assignRocketsToMission(rockets, missionName);

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
        assertThat(savedMission.rockets().values()).containsExactlyInAnyOrder(savedRocket1, savedRocket2, savedRocket3);
        assertThat(savedMission.rockets().keySet()).containsExactlyInAnyOrder(rocket1Name, rocket2Name, rocket3Name);
    }

    @Test
    void shouldAssignEmptySetOfRocketsToMission() {
        //given new mission
        String missionName = "axis-z";
        dragonRocketsRepository.addMission(missionName);

        //and empty set of rockets
        Set<String> rockets = Set.of();

        //when empty set of rockets is assigned to mission
        dragonRocketsRepository.assignRocketsToMission(rockets, missionName);

        //then mission has no rockets
        Optional<Mission> optionalMission = missionRepository.findByName(missionName);
        assertThat(optionalMission).isNotEmpty();
        Mission savedMission = optionalMission.get();
        assertThat(savedMission.rockets()).hasSize(0);
    }

    @Test
    void shouldThrowExceptionWhenAssigningRocketsWhereOneAlreadyHasMission() {
        //given new mission
        String missionName = "axis-b";
        String anotherMission = "axis-bc";
        Mission mission = dragonRocketsRepository.addMission(missionName);
        dragonRocketsRepository.addMission(anotherMission);

        //and rockets where one already has a mission
        String rocket1Name = "fast-n1";
        String rocket2Name = "fast-n2";
        Rocket rocket1 = dragonRocketsRepository.addRocket(rocket1Name);
        dragonRocketsRepository.addRocket(rocket2Name);
        dragonRocketsRepository.assignRocketToMission(rocket2Name, anotherMission);
        Rocket rocket2 = rocketRepository.findByName(rocket2Name).get();
        Set<String> rockets = Set.of(rocket1Name, rocket2Name);

        //when rockets are assigned to mission
        assertThatThrownBy(() -> dragonRocketsRepository.assignRocketsToMission(rockets, missionName))
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

        String anotherMission = "axis-xs";
        dragonRocketsRepository.addMission(anotherMission);

        //and rockets where multiple already have missions
        String rocket1Name = "fast-o1";
        String rocket2Name = "fast-o2";
        String rocket3Name = "fast-o3";
        dragonRocketsRepository.addRocket(rocket1Name);
        dragonRocketsRepository.assignRocketToMission(rocket1Name, anotherMission);
        dragonRocketsRepository.addRocket(rocket2Name);
        dragonRocketsRepository.addRocket(rocket3Name);
        dragonRocketsRepository.assignRocketToMission(rocket3Name, anotherMission);
        Set<String> rockets = Set.of(rocket1Name, rocket2Name, rocket3Name);
        mission = missionRepository.findByName(missionName).get();

        //when rockets are assigned to mission
        assertThatThrownBy(() -> dragonRocketsRepository.assignRocketsToMission(rockets, missionName))
                .isExactlyInstanceOf(IllegalStateException.class);

        //and no updates were made to db
        assertThat(rocketRepository.findByName(rocket1Name)).hasValueSatisfying(rocket ->
                assertThat(rocket.missionName()).hasValue(anotherMission));
        assertThat(rocketRepository.findByName(rocket2Name)).hasValueSatisfying(rocket ->
                assertThat(rocket.missionName()).isEmpty());
        assertThat(rocketRepository.findByName(rocket3Name)).hasValueSatisfying(rocket ->
                assertThat(rocket.missionName()).hasValue(anotherMission));
        assertThat(missionRepository.findByName(missionName)).hasValue(mission);
    }

    @Test
    void shouldAssignSingleRocketToMissionUsingGroupMethod() {
        //given new mission
        String missionName = "axis-d";
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //and single rocket in a set
        String rocketName = "fast-single";
        dragonRocketsRepository.addRocket(rocketName);
        Set<String> rockets = Set.of(rocketName);

        //when single rocket in set is assigned to mission
        dragonRocketsRepository.assignRocketsToMission(rockets, missionName);

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
        assertThat(savedMission.rockets().get(rocketName)).isEqualTo(savedRocket);
    }

    @Test
    void shouldSetRocketAsDamaged() {
        //given new rocket
        String rocketName = "fast-o";
        Rocket rocket = dragonRocketsRepository.addRocket(rocketName);
        assertThat(rocket.status()).isEqualTo(RocketStatus.ON_GROUND);

        //when rocket is set as damaged
        dragonRocketsRepository.setRocketAsDamaged(rocketName);

        //then rocket status is updated
        Optional<Rocket> optionalRocket = rocketRepository.findByName(rocketName);
        assertThat(optionalRocket).isNotEmpty();
        Rocket savedRocket = optionalRocket.get();
        assertThat(savedRocket.missionName()).isEmpty();
        assertThat(savedRocket.name()).isEqualTo(rocketName);
        assertThat(savedRocket.status()).isEqualTo(RocketStatus.IN_REPAIR);
    }

    @Test
    void shouldSetRocketAsDamagedWhenRocketHasMissionAssigned() {
        //given mission
        String missionName = "mission-n";
        dragonRocketsRepository.addMission(missionName);

        //and rocket
        String rocketName = "forward-x";
        dragonRocketsRepository.addRocket(rocketName);

        //when rocket is set as damaged
        dragonRocketsRepository.assignRocketToMission(rocketName, missionName);
        dragonRocketsRepository.setRocketAsDamaged(rocketName);

        //then rocket is updated
        Optional<Rocket> optionalRocket = rocketRepository.findByName(rocketName);
        assertThat(optionalRocket).isNotEmpty();
        Rocket savedRocket = optionalRocket.get();
        assertThat(savedRocket.missionName()).hasValue(missionName);
        assertThat(savedRocket.name()).isEqualTo(rocketName);
        assertThat(savedRocket.status()).isEqualTo(RocketStatus.IN_REPAIR);

        //and mission is updated
        Mission savedMission = missionRepository.findByName(missionName).get();
        assertThat(savedMission.rockets().get(rocketName)).isEqualTo(savedRocket);
    }

    @Test
    void shouldSetRocketAsRepaired() {
        //given new rocket
        String rocketName = "fast-p";
        dragonRocketsRepository.addRocket(rocketName);

        //and rocket is set as damaged first
        dragonRocketsRepository.setRocketAsDamaged(rocketName);

        //verify rocket is damaged
        Optional<Rocket> damagedRocket = rocketRepository.findByName(rocketName);
        assertThat(damagedRocket).isNotEmpty();
        assertThat(damagedRocket.get().status()).isEqualTo(RocketStatus.IN_REPAIR);

        //when rocket is set as repaired
        dragonRocketsRepository.setRocketAsRepaired(rocketName);

        //then rocket status is updated
        Optional<Rocket> optionalRocket = rocketRepository.findByName(rocketName);
        assertThat(optionalRocket).isNotEmpty();
        Rocket savedRocket = optionalRocket.get();
        assertThat(savedRocket.missionName()).isEmpty();
        assertThat(savedRocket.name()).isEqualTo(rocketName);
        assertThat(savedRocket.status()).isEqualTo(RocketStatus.ON_GROUND);
    }

    @Test
    void shouldSetRocketAsRepairedWhenRocketHasMissionAssigned() {
        //given mission
        String missionName = "mission-o";
        dragonRocketsRepository.addMission(missionName);

        //and rocket
        String rocketName = "forward-y";
        dragonRocketsRepository.addRocket(rocketName);

        //and rocket is assigned to mission and then damaged
        dragonRocketsRepository.assignRocketToMission(rocketName, missionName);
        dragonRocketsRepository.setRocketAsDamaged(rocketName);

        //verify rocket is damaged and assigned to mission
        Optional<Rocket> damagedRocket = rocketRepository.findByName(rocketName);
        assertThat(damagedRocket).isNotEmpty();
        assertThat(damagedRocket.get().status()).isEqualTo(RocketStatus.IN_REPAIR);
        assertThat(damagedRocket.get().missionName()).hasValue(missionName);

        //when rocket is set as repaired
        dragonRocketsRepository.setRocketAsRepaired(rocketName);

        //then rocket is updated
        Optional<Rocket> optionalRocket = rocketRepository.findByName(rocketName);
        assertThat(optionalRocket).isNotEmpty();
        Rocket savedRocket = optionalRocket.get();
        assertThat(savedRocket.missionName()).hasValue(missionName);
        assertThat(savedRocket.name()).isEqualTo(rocketName);
        assertThat(savedRocket.status()).isEqualTo(RocketStatus.IN_SPACE);

        //and mission is updated
        Mission savedMission = missionRepository.findByName(missionName).get();
        assertThat(savedMission.rockets().get(rocketName)).isEqualTo(savedRocket);
    }

    @Test
    void shouldNotChangeRocketWhenAlreadyDamaged() {
        //given new rocket
        String rocketName = "already-damaged";
        dragonRocketsRepository.addRocket(rocketName);

        //and rocket is already set as damaged
        dragonRocketsRepository.setRocketAsDamaged(rocketName);

        //verify rocket is damaged
        Optional<Rocket> damagedRocket = rocketRepository.findByName(rocketName);
        assertThat(damagedRocket).isNotEmpty();
        assertThat(damagedRocket.get().status()).isEqualTo(RocketStatus.IN_REPAIR);
        Rocket rocketBeforeSecondCall = damagedRocket.get();

        //when rocket is set as damaged again
        dragonRocketsRepository.setRocketAsDamaged(rocketName);

        //then rocket status remains the same
        Optional<Rocket> optionalRocket = rocketRepository.findByName(rocketName);
        assertThat(optionalRocket).isNotEmpty();
        Rocket savedRocket = optionalRocket.get();
        assertThat(savedRocket.status()).isEqualTo(RocketStatus.IN_REPAIR);
        assertThat(savedRocket).isEqualTo(rocketBeforeSecondCall);
        assertThat(savedRocket.missionName()).isEmpty();
    }

    @Test
    void shouldNotChangeRocketWhenAlreadyRepaired() {
        //given new rocket
        String rocketName = "already-repaired";
        dragonRocketsRepository.addRocket(rocketName);

        //verify rocket starts in repaired state (ON_GROUND)
        Optional<Rocket> initialRocket = rocketRepository.findByName(rocketName);
        assertThat(initialRocket).isNotEmpty();
        assertThat(initialRocket.get().status()).isEqualTo(RocketStatus.ON_GROUND);
        Rocket rocketBeforeCall = initialRocket.get();

        //when rocket is set as repaired (but it's already in repaired state)
        dragonRocketsRepository.setRocketAsRepaired(rocketName);

        //then rocket status remains the same
        Optional<Rocket> optionalRocket = rocketRepository.findByName(rocketName);
        assertThat(optionalRocket).isNotEmpty();
        Rocket savedRocket = optionalRocket.get();
        assertThat(savedRocket.status()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(savedRocket).isEqualTo(rocketBeforeCall);
        assertThat(savedRocket.missionName()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenMissionDoesNotExist() {
        //given non-existing mission name
        String nonExistingMissionName = "non-existing-mission";

        //when trying to end mission that does not exist
        //then exception is thrown
        assertThatThrownBy(() -> dragonRocketsRepository.endMission(nonExistingMissionName))
                .isExactlyInstanceOf(MissionDoesNotExistException.class);
    }

    @Test
    void shouldEndMissionWhenItHasRocketsAssigned() {
        //given mission with rockets
        String missionName = "mission-to-end-1";
        dragonRocketsRepository.addMission(missionName);

        String rocket1Name = "rocket-end-1";
        String rocket2Name = "rocket-end-2";
        dragonRocketsRepository.addRocket(rocket1Name);
        dragonRocketsRepository.addRocket(rocket2Name);

        dragonRocketsRepository.assignRocketToMission(rocket1Name, missionName);
        dragonRocketsRepository.assignRocketToMission(rocket2Name, missionName);

        //verify rockets are assigned before ending mission
        Optional<Rocket> assignedRocket1 = rocketRepository.findByName(rocket1Name);
        Optional<Rocket> assignedRocket2 = rocketRepository.findByName(rocket2Name);
        assertThat(assignedRocket1.get().missionName()).hasValue(missionName);
        assertThat(assignedRocket2.get().missionName()).hasValue(missionName);

        //when mission is ended
        dragonRocketsRepository.endMission(missionName);

        //then mission is marked as ended
        Optional<Mission> optionalMission = missionRepository.findByName(missionName);
        assertThat(optionalMission).isNotEmpty();
        Mission endedMission = optionalMission.get();
        assertThat(endedMission.isEnded()).isTrue();
        assertThat(endedMission.rockets()).isEmpty();

        //and rockets are de-assigned from mission
        Optional<Rocket> deassignedRocket1 = rocketRepository.findByName(rocket1Name);
        Optional<Rocket> deassignedRocket2 = rocketRepository.findByName(rocket2Name);
        assertThat(deassignedRocket1).isNotEmpty();
        assertThat(deassignedRocket2).isNotEmpty();
        assertThat(deassignedRocket1.get().missionName()).isEmpty();
        assertThat(deassignedRocket2.get().missionName()).isEmpty();

        //and rockets status is updated
        assertThat(deassignedRocket1.get().status()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(deassignedRocket2.get().status()).isEqualTo(RocketStatus.ON_GROUND);
    }

    @Test
    void shouldEndMissionIfNoRocketsAssigned() {
        //given mission with no rockets
        String missionName = "mission-to-end-2";
        Mission mission = dragonRocketsRepository.addMission(missionName);

        //verify mission has no rockets
        assertThat(mission.rockets()).isEmpty();

        //when mission is ended
        dragonRocketsRepository.endMission(missionName);

        //then mission is marked as ended
        Optional<Mission> optionalMission = missionRepository.findByName(missionName);
        assertThat(optionalMission).isNotEmpty();
        Mission endedMission = optionalMission.get();
        assertThat(endedMission.isEnded()).isTrue();
        assertThat(endedMission.rockets()).isEmpty();
    }

    @Test
    void shouldReturnDragonsSummarySortedByRocketCountAndMissionName() {
        //given multiple missions with different rocket counts
        String mission1Name = "alpha-mission";
        String mission2Name = "beta-mission";
        String mission3Name = "gamma-mission";
        String mission4Name = "delta-mission";

        dragonRocketsRepository.addMission(mission1Name); // will have 3 rockets
        dragonRocketsRepository.addMission(mission2Name); // will have 1 rocket
        dragonRocketsRepository.addMission(mission3Name); // will have 3 rockets
        dragonRocketsRepository.addMission(mission4Name); // will have 0 rockets

        //and rockets assigned to missions
        Rocket rocket1 = dragonRocketsRepository.addRocket("rocket-1");
        Rocket rocket2 = dragonRocketsRepository.addRocket("rocket-2");
        Rocket rocket3 = dragonRocketsRepository.addRocket("rocket-3");
        Rocket rocket4 = dragonRocketsRepository.addRocket("rocket-4");
        Rocket rocket5 = dragonRocketsRepository.addRocket("rocket-5");
        Rocket rocket6 = dragonRocketsRepository.addRocket("rocket-6");
        Rocket rocket7 = dragonRocketsRepository.addRocket("rocket-7");

        // Assign rockets to create different counts
        dragonRocketsRepository.assignRocketToMission(rocket1.name(), mission1Name);
        dragonRocketsRepository.assignRocketToMission(rocket2.name(), mission1Name);
        dragonRocketsRepository.assignRocketToMission(rocket3.name(), mission1Name); // mission1: 3 rockets
        dragonRocketsRepository.setRocketAsDamaged(rocket1.name());

        dragonRocketsRepository.assignRocketToMission(rocket4.name(), mission2Name); // mission2: 1 rocket

        dragonRocketsRepository.assignRocketToMission(rocket5.name(), mission3Name);
        dragonRocketsRepository.assignRocketToMission(rocket6.name(), mission3Name);
        dragonRocketsRepository.assignRocketToMission(rocket7.name(), mission3Name); // mission3: 3 rockets

        // mission4: 0 rockets

        //when getting dragons summary
        Summary summary = dragonRocketsRepository.getDragonsSummary();

        //then missions are sorted by rocket count descending, then by name descending
        List<Mission> sortedMissions = summary.missions();
        assertThat(sortedMissions).hasSize(4);

        // First: missions with 3 rockets, sorted by name descending (gamma before alpha)
        assertThat(sortedMissions.get(0).name()).isEqualTo(mission3Name); // gamma-mission (3 rockets)
        assertThat(sortedMissions.get(0).rockets()).hasSize(3);

        assertThat(sortedMissions.get(1).name()).isEqualTo(mission1Name); // alpha-mission (3 rockets)
        assertThat(sortedMissions.get(1).rockets()).hasSize(3);

        // Third: mission with 1 rocket
        assertThat(sortedMissions.get(2).name()).isEqualTo(mission2Name); // beta-mission (1 rocket)
        assertThat(sortedMissions.get(2).rockets()).hasSize(1);

        // Last: mission with 0 rockets
        assertThat(sortedMissions.get(3).name()).isEqualTo(mission4Name); // delta-mission (0 rockets)
        assertThat(sortedMissions.get(3).rockets()).hasSize(0);
    }

    @Test
    void shouldReturnEmptySummaryWhenNoMissionsExist() {
        //given no missions exist

        //when getting dragons summary
        Summary summary = dragonRocketsRepository.getDragonsSummary();

        //then summary contains empty list
        List<Mission> sortedMissions = summary.missions();
        assertThat(sortedMissions).isEmpty();
    }

    @Test
    void shouldReturnSummaryWithMissionsHavingSameRocketCountSortedByName() {
        //given multiple missions with same rocket count
        String mission1Name = "charlie-mission";
        String mission2Name = "alpha-mission";
        String mission3Name = "bravo-mission";

        dragonRocketsRepository.addMission(mission1Name);
        dragonRocketsRepository.addMission(mission2Name);
        dragonRocketsRepository.addMission(mission3Name);

        //and all missions have same number of rockets (2 each)
        Rocket rocket1 = dragonRocketsRepository.addRocket("rocket-a");
        Rocket rocket2 = dragonRocketsRepository.addRocket("rocket-b");
        Rocket rocket3 = dragonRocketsRepository.addRocket("rocket-c");
        Rocket rocket4 = dragonRocketsRepository.addRocket("rocket-d");
        Rocket rocket5 = dragonRocketsRepository.addRocket("rocket-e");
        Rocket rocket6 = dragonRocketsRepository.addRocket("rocket-f");

        dragonRocketsRepository.assignRocketToMission(rocket1.name(), mission1Name);
        dragonRocketsRepository.assignRocketToMission(rocket2.name(), mission1Name);

        dragonRocketsRepository.assignRocketToMission(rocket3.name(), mission2Name);
        dragonRocketsRepository.assignRocketToMission(rocket4.name(), mission2Name);

        dragonRocketsRepository.assignRocketToMission(rocket5.name(), mission3Name);
        dragonRocketsRepository.assignRocketToMission(rocket6.name(), mission3Name);

        //when getting dragons summary
        Summary summary = dragonRocketsRepository.getDragonsSummary();

        //then missions are sorted by name descending (since rocket count is same)
        List<Mission> sortedMissions = summary.missions();
        assertThat(sortedMissions).hasSize(3);

        assertThat(sortedMissions.get(0).name()).isEqualTo(mission1Name);
        assertThat(sortedMissions.get(1).name()).isEqualTo(mission3Name);
        assertThat(sortedMissions.get(2).name()).isEqualTo(mission2Name);

        // All should have same rocket count
        assertThat(sortedMissions.get(0).rockets()).hasSize(2);
        assertThat(sortedMissions.get(1).rockets()).hasSize(2);
        assertThat(sortedMissions.get(2).rockets()).hasSize(2);
    }
}