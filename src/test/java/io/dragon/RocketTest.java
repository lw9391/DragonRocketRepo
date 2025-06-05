package io.dragon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


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
}