package com.youlai.system.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QualityGradeAllocationTest {
    @Test
    void usesFinalRosterAndLargestRemainderForConfiguredPercentages() {
        assertThat(StandaloneQualityEvaluationController.allocateGradeCounts(44, .60, .35, .05))
                .containsExactly(27, 15, 2);
        assertThat(StandaloneQualityEvaluationController.allocateGradeCounts(2, .60, .35, .05))
                .containsExactly(1, 1, 0);
        assertThat(StandaloneQualityEvaluationController.allocateGradeCounts(20, .60, .35, .05))
                .containsExactly(12, 7, 1);
    }
}
