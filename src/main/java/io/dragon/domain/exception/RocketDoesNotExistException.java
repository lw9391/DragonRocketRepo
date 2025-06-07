package io.dragon.domain.exception;

public class RocketDoesNotExistException extends RuntimeException {

    private static final String MESSAGE = "Rocket with name %s does not exist in the system";

    public RocketDoesNotExistException(String rocketName) {
        super(MESSAGE.formatted(rocketName));
    }
}
