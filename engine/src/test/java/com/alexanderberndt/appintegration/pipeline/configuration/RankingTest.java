package com.alexanderberndt.appintegration.pipeline.configuration;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankingTest {

    @Test
    void getOverrulingRanks() {
        assertEquals(Collections.singleton(Ranking.GLOBAL), Ranking.PIPELINE_EXECUTION.getOverrulingRanks());
        assertEquals(new HashSet<>(Arrays.asList(Ranking.GLOBAL, Ranking.PIPELINE_EXECUTION)), Ranking.PIPELINE_DEFINITION.getOverrulingRanks());
    }

    @Test
    void ordinal() {
        assertTrue(Ranking.PIPELINE_EXECUTION.ordinal() < Ranking.TASK_DEFAULT.ordinal());
        assertTrue(Ranking.PIPELINE_DEFINITION.ordinal() < Ranking.TASK_DEFAULT.ordinal());
        assertTrue(Ranking.PIPELINE_EXECUTION.ordinal() < Ranking.PIPELINE_DEFINITION.ordinal());
    }

    @Test
    void values() {
        // test for growing ordinals
        int curOrdinal = -1;
        for (Ranking rank : Ranking.values()) {
            curOrdinal++;
            assertEquals(curOrdinal, rank.ordinal());
        }
    }

}