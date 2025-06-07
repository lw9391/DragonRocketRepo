package io.dragon.domain;

import io.dragon.dataaccess.InMemoryRocketRepository;
import io.dragon.domain.exception.RocketAlreadyExistsException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


class SpaceXDragonRocketsRepositoryTest {

    RocketRepository rocketRepository = new InMemoryRocketRepository();
    SpaceXDragonRocketsRepository dragonRocketsRepository = new SpaceXDragonRocketsRepository(rocketRepository);

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
}