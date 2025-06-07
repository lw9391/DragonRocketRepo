package io.dragon.domain;

import java.util.List;

public record Summary(List<Mission> missions) {

    public void printSummary() {
        missions.forEach(mission -> System.out.println(mission.printToStatus()));
    }

}
