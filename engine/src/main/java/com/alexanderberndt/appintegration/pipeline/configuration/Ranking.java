package com.alexanderberndt.appintegration.pipeline.configuration;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Ranking {

    GLOBAL, PIPELINE_EXECUTION, PIPELINE_DEFINITION, TASK_DEFAULT;

    public Set<Ranking> getOverrulingRanks() {
        return Arrays.stream(Ranking.values())
                .filter(otherRanking -> otherRanking.ordinal() < this.ordinal())
                .collect(Collectors.toSet());
    }


}
