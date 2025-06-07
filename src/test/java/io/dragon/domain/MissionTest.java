package io.dragon.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
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

}