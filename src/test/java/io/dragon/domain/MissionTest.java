package io.dragon.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MissionTest {

    static final String MISSION_NAME = "sirius";

    @Test
    void shouldCreateNewMissionWithExpectedState() {
        //when new mission is created
        Mission mission = Mission.create(MISSION_NAME);

        //then
        assertThat(mission.name()).isEqualTo(MISSION_NAME);
        assertThat(mission.rockets()).isEmpty();
    }
}