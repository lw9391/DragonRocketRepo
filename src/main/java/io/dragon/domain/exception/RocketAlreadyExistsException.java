package io.dragon.domain.exception;

public class RocketAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE = "Rocket with name %s already exists in the system";

    public RocketAlreadyExistsException(String rocketName) {
        super(MESSAGE.formatted(rocketName));
    }
}
