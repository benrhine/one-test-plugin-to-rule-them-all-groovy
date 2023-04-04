package com.benrhine.plugins.v1.internal.executer;

final class RoundResult {

    final TestNames failedTests;
    final TestNames nonRetriedTests;
    final boolean lastRound;
    final boolean hasRetryFilteredFailures;

    RoundResult(TestNames failedTests, TestNames nonRetriedTests, boolean lastRound, boolean hasRetryFilteredFailures) {
        this.failedTests = failedTests;
        this.nonRetriedTests = nonRetriedTests;
        this.lastRound = lastRound;
        this.hasRetryFilteredFailures = hasRetryFilteredFailures;
    }
}
