package io.dragon.domain;

public enum RocketStatus {

    ON_GROUND("On ground"),
    IN_SPACE("In space"),
    IN_REPAIR("In repair");

    public final String printableName;

    RocketStatus(String printableName) {
        this.printableName = printableName;
    }

}
