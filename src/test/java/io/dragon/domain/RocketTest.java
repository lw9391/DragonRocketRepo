package io.dragon.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class RocketTest {

    static final String ROCKET_NAME = "draco 1";

    @Test
    void shouldCreateNewRocketWithExpectedState() {
        //when new rocket is created
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME);

        //then
        assertThat(rocket.name()).isEqualTo(ROCKET_NAME);
        assertThat(rocket.status()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(rocket.missionName()).isEmpty();
    }

    @Test
    void shouldSetRocketStatusToInRepair() {
        //when new rocket is created
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME);

        //and rocket is set to in repair status
        Rocket inRepair = rocket.inRepair();

        //then
        assertThat(inRepair.name()).isEqualTo(rocket.name());
        assertThat(inRepair.status()).isEqualTo(RocketStatus.IN_REPAIR);
        assertThat(inRepair.missionName()).isEqualTo(rocket.missionName());
    }

    @Test
    void shouldAssignMissionToRocketOnGround() {
        // given
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME);
        Mission mission = Mission.create("Mars exploration");

        // when
        Rocket rocketWithMission = rocket.assignMission(mission.name());

        // then
        assertThat(rocketWithMission.name()).isEqualTo(rocket.name());
        assertThat(rocketWithMission.status()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(rocketWithMission.missionName()).isPresent();
        assertThat(rocketWithMission.missionName().get()).isEqualTo(mission.name());
    }

    @Test
    void shouldAssignMissionToRocketInRepairAndKeepRepairStatus() {
        // given
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME).inRepair();
        Mission mission = Mission.create("Moon landing");

        // when
        Rocket rocketWithMission = rocket.assignMission(mission.name());

        // then
        assertThat(rocketWithMission.name()).isEqualTo(rocket.name());
        assertThat(rocketWithMission.status()).isEqualTo(RocketStatus.IN_REPAIR);
        assertThat(rocketWithMission.missionName()).isPresent();
        assertThat(rocketWithMission.missionName().get()).isEqualTo(mission.name());
    }

    @Test
    void shouldThrowExceptionWhenAssigningMissionToRocketThatAlreadyHasMission() {
        // given
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME);
        Mission firstMission =  Mission.create("Mars exploration");
        Mission secondMission = Mission.create("Moon landing");
        Rocket rocketWithMission = rocket.assignMission(firstMission.name());

        // when & then
        assertThatThrownBy(() -> rocketWithMission.assignMission(secondMission.name()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Mission already assigned");
    }

}