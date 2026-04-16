package web.sportflow.challenge;

public record ChallengeProgress(
    double currentValue, double targetValue, int percentage, boolean completed, String unitLabel) {}
