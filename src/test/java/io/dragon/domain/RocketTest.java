package io.dragon.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

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
    void shouldAssignMissionToRocket() {
        // given
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME);
        Mission mission = Mission.create("Mars exploration");

        // when
        Rocket rocketWithMission = rocket.assignMission(mission.name());

        // then
        assertThat(rocketWithMission.name()).isEqualTo(rocket.name());
        assertThat(rocketWithMission.status()).isEqualTo(rocket.status());
        assertThat(rocketWithMission.missionName()).isPresent();
        assertThat(rocketWithMission.missionName().get()).isEqualTo(mission.name());
    }

    @Test
    void shouldThrowExceptionWhenAssigningMissionToRocketThatAlreadyHasMission() {
        // given
        Rocket rocket = Rocket.createNewRocket(ROCKET_NAME);
        Mission firstMission = Mission.create("Mars exploration");
        Mission secondMission = Mission.create("Moon landing");
        Rocket rocketWithMission = rocket.assignMission(firstMission.name());

        // when & then
        assertThatThrownBy(() -> rocketWithMission.assignMission(secondMission.name()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Mission already assigned");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ON_GROUND", "IN_REPAIR"})
    void shouldSetRocketStatusToRocketWithoutMission(String statusName) {
        //when new rocket is created
        Rocket rocket = new Rocket(ROCKET_NAME, RocketStatus.IN_REPAIR, Optional.empty());

        //and rocket status is set
        RocketStatus newStatus = RocketStatus.valueOf(statusName);
        Rocket updated = rocket.setStatus(newStatus);

        //then
        assertThat(updated.name()).isEqualTo(rocket.name());
        assertThat(updated.status()).isEqualTo(newStatus);
        assertThat(updated.missionName()).isEqualTo(rocket.missionName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ON_GROUND", "IN_REPAIR", "IN_SPACE"})
    void shouldSetRocketStatusToRocketWithMissionAssigned(String statusName) {
        //when new rocket is created
        Rocket rocket = new Rocket(ROCKET_NAME, RocketStatus.IN_REPAIR, Optional.of("some-mission"));

        //and rocket status is set
        RocketStatus newStatus = RocketStatus.valueOf(statusName);
        Rocket updated = rocket.setStatus(newStatus);

        //then
        assertThat(updated.name()).isEqualTo(rocket.name());
        assertThat(updated.status()).isEqualTo(newStatus);
        assertThat(updated.missionName()).isEqualTo(rocket.missionName());
    }

    @Test
    void shouldThrowExceptionWhenSettingOnSpaceStatusToRocketWithoutMission() {
        // given
        //when new rocket is created
        Rocket rocket = new Rocket(ROCKET_NAME, RocketStatus.IN_REPAIR, Optional.empty());

        // when & then
        assertThatThrownBy(() -> rocket.setStatus(RocketStatus.IN_SPACE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot send rocket to space without mission");
    }

    @Test
    void shouldRemoveMission() {
        // given
        Rocket rocket = new Rocket(ROCKET_NAME, RocketStatus.IN_SPACE, Optional.of("space program"));

        // when
        Rocket rocketWithMission = rocket.removeMission();

        // then
        assertThat(rocketWithMission.name()).isEqualTo(rocket.name());
        assertThat(rocketWithMission.status()).isEqualTo(RocketStatus.ON_GROUND);
        assertThat(rocketWithMission.missionName()).isEmpty();
    }

    @Test
    void shouldRemoveMissionFromDamagedRocket() {
        // given
        Rocket rocket = new Rocket(ROCKET_NAME, RocketStatus.IN_REPAIR, Optional.of("space program"));

        // when
        Rocket rocketWithMission = rocket.removeMission();

        // then
        assertThat(rocketWithMission.name()).isEqualTo(rocket.name());
        assertThat(rocketWithMission.status()).isEqualTo(RocketStatus.IN_REPAIR);
        assertThat(rocketWithMission.missionName()).isEmpty();
    }


}