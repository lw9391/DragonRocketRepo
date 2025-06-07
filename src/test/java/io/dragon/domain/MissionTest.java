package io.dragon.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MissionTest {

    static final String MISSION_NAME = "sirius";

    @Test
    void shouldCreateNewMissionWithExpectedState() {
        //when new mission is created
        Mission mission = Mission.create(MISSION_NAME);

        //then
        assertThat(mission.name()).isEqualTo(MISSION_NAME);
        assertThat(mission.rockets()).isEmpty();
        assertThat(mission.isEnded()).isFalse();
        assertThat(mission.status()).isEqualTo(MissionStatus.SCHEDULED);
    }

    @Test
    void shouldAssignRocketToMission() {
        //given new mission
        Mission mission = Mission.create(MISSION_NAME);

        //when rocket is assigned
        Rocket rocket = Rocket.createNewRocket("lion");
        Mission withRocket = mission.assignRocket(rocket);

        //then
        assertThat(withRocket.rockets()).hasSize(1);
        assertThat(withRocket.rockets()).containsKey(rocket.name());
        assertThat(withRocket.rockets()).containsValue(rocket);
        assertThat(withRocket.name()).isEqualTo(MISSION_NAME);
    }

    @ParameterizedTest(name = "Should end mission {0}")
    @MethodSource("missions")
    void shouldEndMission(Mission mission) {
        //given mission

        //when mission is ended
        Mission ended = mission.endMission();

        //then
        assertThat(ended.name()).isEqualTo(mission.name());
        assertThat(ended.rockets()).isEmpty();
        assertThat(ended.isEnded()).isTrue();
        assertThat(ended.status()).isEqualTo(MissionStatus.ENDED);
    }

    static Stream<Mission> missions() {
        return Stream.of(
                new Mission("without-rockets", Map.of(), false),
                new Mission("with-rockets-assigned", Map.of("Falcon", Rocket.createNewRocket("Falcon")), false)
        );
    }

    @Test
    void shouldThrowExceptionWhenTryingToEndAlreadyEndedMission() {
        // given
        Mission mission = Mission.create(MISSION_NAME);
        Mission endedMission = mission.endMission();

        // when & then
        assertThatThrownBy(endedMission::endMission)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Mission is already ended");
    }

    @Test
    void shouldReturnInProgressStatusForMissionWithHealthyRockets() {
        // given
        Mission mission = Mission.create(MISSION_NAME);
        Rocket healthyRocket1 = Rocket.createNewRocket("falcon");
        Rocket healthyRocket2 = Rocket.createNewRocket("dragon");
        Mission missionWithRockets = mission.assignRocket(healthyRocket1).assignRocket(healthyRocket2);

        // when
        MissionStatus status = missionWithRockets.status();

        // then
        assertThat(status).isEqualTo(MissionStatus.IN_PROGRESS);
    }

    @Test
    void shouldReturnPendingStatusForMissionWithDamagedRocket() {
        // given
        Mission mission = Mission.create(MISSION_NAME);
        Rocket healthyRocket = Rocket.createNewRocket("falcon");
        Rocket damagedRocket = Rocket.createNewRocket("dragon").inRepair();
        Mission missionWithRockets = mission.assignRocket(healthyRocket)
                .assignRocket(damagedRocket);

        // when
        MissionStatus status = missionWithRockets.status();

        // then
        assertThat(status).isEqualTo(MissionStatus.PENDING);
    }

    @Test
    void shouldReturnPendingStatusForMissionWithOnlyDamagedRockets() {
        // given
        Mission mission = Mission.create(MISSION_NAME);
        Rocket damagedRocket1 = Rocket.createNewRocket("falcon").inRepair();
        Rocket damagedRocket2 = Rocket.createNewRocket("dragon").inRepair();
        Mission missionWithRockets = mission.assignRocket(damagedRocket1).assignRocket(damagedRocket2);

        // when
        MissionStatus status = missionWithRockets.status();

        // then
        assertThat(status).isEqualTo(MissionStatus.PENDING);
    }

    @Test
    void shouldReturnEndedStatusForEndedMission() {
        // given
        Mission mission = Mission.create(MISSION_NAME);
        Rocket rocket = Rocket.createNewRocket("falcon");
        Mission missionWithRocket = mission.assignRocket(rocket);
        Mission endedMission = missionWithRocket.endMission();

        // when
        MissionStatus status = endedMission.status();

        // then
        assertThat(status).isEqualTo(MissionStatus.ENDED);
    }

    @Test
    void shouldUpdateRocketInMission() {
        //given mission with assigned rocket
        String missionName = "update-mission-1";
        String rocketName = "updatable-rocket";
        Rocket originalRocket = new Rocket(rocketName, RocketStatus.IN_SPACE, Optional.of(missionName));
        Mission mission = Mission.create(missionName).assignRocket(originalRocket);

        //and updated rocket with new status
        Rocket updatedRocket = new Rocket(rocketName, RocketStatus.IN_REPAIR, Optional.of(missionName));

        //when rocket is updated in mission
        Mission missionWithUpdatedRocket = mission.updateRocket(updatedRocket);

        //then mission contains updated rocket
        assertThat(missionWithUpdatedRocket.rockets()).hasSize(1);
        assertThat(missionWithUpdatedRocket.rockets().get(rocketName)).isEqualTo(updatedRocket);
        assertThat(missionWithUpdatedRocket.rockets().get(rocketName).status()).isEqualTo(RocketStatus.IN_REPAIR);
    }

    @Test
    void shouldUpdateOneRocketAmongMultipleRockets() {
        //given mission with multiple rockets
        String missionName = "update-mission-2";
        String rocket1Name = "rocket-1";
        String rocket2Name = "rocket-2";
        String rocket3Name = "rocket-3";

        Rocket rocket1 = new Rocket(rocket1Name, RocketStatus.IN_SPACE, Optional.of(missionName));
        Rocket rocket2 = new Rocket(rocket2Name, RocketStatus.IN_SPACE, Optional.of(missionName));
        Rocket rocket3 = new Rocket(rocket3Name, RocketStatus.IN_SPACE, Optional.of(missionName));

        Mission mission = Mission.create(missionName)
                .assignRocket(rocket1)
                .assignRocket(rocket2)
                .assignRocket(rocket3);

        //and updated version of second rocket
        Rocket updatedRocket2 = new Rocket(rocket2Name, RocketStatus.IN_REPAIR, Optional.of(missionName));

        //when second rocket is updated
        Mission missionWithUpdatedRocket = mission.updateRocket(updatedRocket2);

        //then only second rocket is updated
        assertThat(missionWithUpdatedRocket.rockets()).hasSize(3);
        assertThat(missionWithUpdatedRocket.rockets().get(rocket1Name)).isEqualTo(rocket1);
        assertThat(missionWithUpdatedRocket.rockets().get(rocket2Name)).isEqualTo(updatedRocket2);
        assertThat(missionWithUpdatedRocket.rockets().get(rocket3Name)).isEqualTo(rocket3);

        //and updated rocket has new status
        assertThat(missionWithUpdatedRocket.rockets().get(rocket2Name).status()).isEqualTo(RocketStatus.IN_REPAIR);

        //and other rockets remain unchanged
        assertThat(missionWithUpdatedRocket.rockets().get(rocket1Name).status()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(missionWithUpdatedRocket.rockets().get(rocket3Name).status()).isEqualTo(RocketStatus.IN_SPACE);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRocketNotAssignedToMission() {
        //given mission with one rocket
        String missionName = "update-mission-3";
        String assignedRocketName = "assigned-rocket";
        String unassignedRocketName = "unassigned-rocket";

        Rocket assignedRocket = new Rocket(assignedRocketName, RocketStatus.IN_SPACE, Optional.of(missionName));
        Mission mission = Mission.create(missionName).assignRocket(assignedRocket);

        //and rocket that is not assigned to this mission
        Rocket unassignedRocket = new Rocket(unassignedRocketName, RocketStatus.IN_REPAIR, Optional.empty());

        //when trying to update unassigned rocket
        assertThatThrownBy(() -> mission.updateRocket(unassignedRocket))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rocket is not assigned to the mission");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRocketInEndedMission() {
        //given ended mission with rocket
        String missionName = "update-mission-4";
        String rocketName = "rocket-in-ended-mission";

        Rocket rocket = new Rocket(rocketName, RocketStatus.IN_SPACE, Optional.of(missionName));
        Mission mission = Mission.create(missionName).assignRocket(rocket).endMission();

        //and updated version of the rocket
        Rocket updatedRocket = new Rocket(rocketName, RocketStatus.IN_REPAIR, Optional.of(missionName));

        //when trying to update rocket in ended mission
        assertThatThrownBy(() -> mission.updateRocket(updatedRocket))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Mission is already ended");
    }

    @Test
    void shouldNotUpdateRocketWithDifferentMissionName() {
        //given mission with rocket
        String missionName = "update-mission-5";
        String rocketName = "flexible-rocket";

        Rocket originalRocket = new Rocket(rocketName, RocketStatus.IN_SPACE, Optional.of(missionName));
        Mission mission = Mission.create(missionName).assignRocket(originalRocket);

        //and rocket with different mission name but same rocket name
        Rocket updatedRocket = new Rocket(rocketName, RocketStatus.IN_REPAIR, Optional.of("different-mission"));

        //when rocket is updated, exception is thrown
        assertThatThrownBy(() -> mission.updateRocket(updatedRocket))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldUpdateRocketInEmptyMissionThrowsException() {
        //given mission with no rockets
        String missionName = "empty-mission";
        Mission mission = Mission.create(missionName);

        //and rocket to update
        Rocket rocket = new Rocket("some-rocket", RocketStatus.IN_SPACE, Optional.of(missionName));

        //when trying to update rocket in mission that doesn't have it
        assertThatThrownBy(() -> mission.updateRocket(rocket))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rocket is not assigned to the mission");
    }

}