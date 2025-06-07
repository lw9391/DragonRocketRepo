package io.dragon.domain.exception;

public class MissionAlreadyExistsException extends RuntimeException {

    public static final String MESSAGE = "Mission with name %s already exists in the system";

    public MissionAlreadyExistsException(String missionName) {
        super(MESSAGE.formatted(missionName));
    }
}
