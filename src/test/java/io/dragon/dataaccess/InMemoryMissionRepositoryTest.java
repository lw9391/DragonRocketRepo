package io.dragon.dataaccess;

import io.dragon.domain.Mission;
import io.dragon.domain.Rocket;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class InMemoryMissionRepositoryTest {

    private final InMemoryMissionRepository missionRepository = new InMemoryMissionRepository();

    @Test
    void shouldAddNewMission() {
        //given new mission
        String missionName = "mars 1";
        Mission newMission = Mission.create(missionName);

        //when mission is added to the repository
        missionRepository.save(newMission);

        //then mission is added
        Optional<Mission> addedMission = missionRepository.findByName(missionName);
        assertThat(addedMission).isPresent();
        assertThat(addedMission).hasValue(newMission);
    }

    @Test
    void addMissionShouldThrowExceptionWhenMissionAlreadyExists() {
        //given new mission
        String missionName = "mars 2";
        Mission newMission = Mission.create(missionName);

        //when mission is added to the repository twice
        missionRepository.save(newMission);

        //then second operation fails
        assertThatThrownBy(() -> missionRepository.save(newMission))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mission %s already exists".formatted(missionName));
    }

    @Test
    void shouldUpdateExistingMission() {
        //given new mission
        String missionName = "mars 3";
        Mission newMission = Mission.create(missionName);

        //when mission is added to the repository
        missionRepository.save(newMission);
        Mission updated = newMission.assignRocket(Rocket.createNewRocket("eagle"));

        //then mission is added
        Mission savedUpdated = missionRepository.update(updated);
        assertThat(savedUpdated).isEqualTo(updated);

        //and update mission is saved
        Optional<Mission> inRepository = missionRepository.findByName(missionName);
        assertThat(inRepository).isPresent();
        assertThat(inRepository).hasValue(updated);
    }

    @Test
    void updateMissionShouldThrowExceptionWhenUpdatingNonExistingMission() {
        //given new mission
        String missionName = "mars 4";
        Mission newMission = Mission.create(missionName);

        //when trying to update mission the was not added to repo

        //then exception is thrown

        assertThatThrownBy(() -> missionRepository.update(newMission))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mission %s doesn't exist".formatted(missionName));
    }

    @Test
    void shouldCheckIfMissionExists() {
        //given non-existing mission
        String missionName = "venus 111";

        //when checking if mission exists
        boolean exists = missionRepository.exists(missionName);

        //then exists returns false
        assertThat(exists).isFalse();

        //when mission is added
        missionRepository.save(Mission.create(missionName));

        //then exists return true
        exists = missionRepository.exists(missionName);
        assertThat(exists).isTrue();
    }
}