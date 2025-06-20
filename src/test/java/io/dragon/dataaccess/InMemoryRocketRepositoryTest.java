package io.dragon.dataaccess;

import io.dragon.domain.Rocket;
import io.dragon.domain.RocketStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


class InMemoryRocketRepositoryTest {

    private final InMemoryRocketRepository rocketRepository = new InMemoryRocketRepository();

    @Test
    void shouldAddNewRocket() {
        //given new rocket
        String rocketName = "draco 2";
        Rocket newRocket = Rocket.createNewRocket(rocketName);

        //when rocket is added to the repository
        rocketRepository.save(newRocket);

        //then rocket is added
        Optional<Rocket> addedRocket = rocketRepository.findByName(rocketName);
        assertThat(addedRocket).isPresent();
        assertThat(addedRocket).hasValue(newRocket);
    }

    @Test
    void addRocketShouldThrowExceptionWhenRocketAlreadyExists() {
        //given new rocket
        String rocketName = "draco 3";
        Rocket newRocket = Rocket.createNewRocket(rocketName);

        //when rocket is added to the repository twice
        rocketRepository.save(newRocket);

        //then second operation fails
        assertThatThrownBy(() -> rocketRepository.save(newRocket))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rocket %s already exists".formatted(rocketName));
    }

    @Test
    void shouldUpdateExistingRocket() {
        //given new rocket
        String rocketName = "draco 10";
        Rocket newRocket = Rocket.createNewRocket(rocketName);

        //when rocket is added to the repository
        rocketRepository.save(newRocket);
        Rocket updated = newRocket.setStatus(RocketStatus.IN_REPAIR);

        //then rocket is added
        Rocket savedUpdated = rocketRepository.update(updated);
        assertThat(savedUpdated).isEqualTo(updated);

        //and update rocket is saved
        Optional<Rocket> inRepository = rocketRepository.findByName(rocketName);
        assertThat(inRepository).isPresent();
        assertThat(inRepository).hasValue(updated);
    }

    @Test
    void updateRocketShouldThrowExceptionWhenUpdatingNonExistingRocket() {
        //given new rocket
        String rocketName = "draco 11";
        Rocket newRocket = Rocket.createNewRocket(rocketName);

        //when trying to update rocket the was not added to repo

        //then exception is thrown

        assertThatThrownBy(() -> rocketRepository.update(newRocket))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rocket %s doesn't exist".formatted(rocketName));
    }

    @Test
    void shouldCheckIfRocketExists() {
        //given non-existing rocket
        String rocketName = "draco 111";

        //when checking if rocket exists
        boolean exists = rocketRepository.exists(rocketName);

        //then exists returns false
        assertThat(exists).isFalse();

        //when rocket is added
        rocketRepository.save(Rocket.createNewRocket(rocketName));

        //then exists return true
        exists = rocketRepository.exists(rocketName);
        assertThat(exists).isTrue();
    }
}