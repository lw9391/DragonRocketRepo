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
        assertThat(rocket.mission()).isEmpty();
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
        assertThat(inRepair.mission()).isEqualTo(rocket.mission());
    }

    @Test
    void shouldAssignMissionToRocketOnGround() {
        // given
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME);
        Mission mission = Mission.create("Mars exploration");

        // when
        Rocket rocketWithMission = rocket.assignMission(mission);

        // then
        assertThat(rocketWithMission.name()).isEqualTo(rocket.name());
        assertThat(rocketWithMission.status()).isEqualTo(RocketStatus.IN_SPACE);
        assertThat(rocketWithMission.mission()).isPresent();
        assertThat(rocketWithMission.mission().get()).isEqualTo(mission);
    }

    @Test
    void shouldAssignMissionToRocketInRepairAndKeepRepairStatus() {
        // given
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME).inRepair();
        Mission mission = Mission.create("Moon landing");

        // when
        Rocket rocketWithMission = rocket.assignMission(mission);

        // then
        assertThat(rocketWithMission.name()).isEqualTo(rocket.name());
        assertThat(rocketWithMission.status()).isEqualTo(RocketStatus.IN_REPAIR);
        assertThat(rocketWithMission.mission()).isPresent();
        assertThat(rocketWithMission.mission().get()).isEqualTo(mission);
    }

    @Test
    void shouldThrowExceptionWhenAssigningMissionToRocketThatAlreadyHasMission() {
        // given
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME);
        Mission firstMission =  Mission.create("Mars exploration");
        Mission secondMission = Mission.create("Moon landing");
        Rocket rocketWithMission = rocket.assignMission(firstMission);

        // when & then
        assertThatThrownBy(() -> rocketWithMission.assignMission(secondMission))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Mission already assigned");
    }

}