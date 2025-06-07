package io.dragon.domain.exception;

public class MissionDoesNotExistException extends RuntimeException {

    public static final String MESSAGE = "Mission with name %s does not exist in the system";

    public MissionDoesNotExistException(String missionName) {
        super(MESSAGE.formatted(missionName));
    }
}
