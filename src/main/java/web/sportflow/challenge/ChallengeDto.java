package web.sportflow.challenge;

public record ChallengeDto(
    double currentValue, double targetValue, int percentage, boolean completed, String unitLabel) {}
