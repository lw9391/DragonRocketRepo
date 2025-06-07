package io.dragon.domain;

public enum MissionStatus {
    SCHEDULED("Scheduled"),
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    ENDED("Ended");

    public final String printableName;

    MissionStatus(String printableName) {
        this.printableName = printableName;
    }
}
