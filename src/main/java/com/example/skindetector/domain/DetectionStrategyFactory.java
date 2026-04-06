package com.example.skindetector.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factory and Registry for skin detection strategies.
 * Enables dynamic strategy selection at runtime.
 */
@Component
public class DetectionStrategyFactory {

    private final Map<String, SkinDetectionStrategy> strategies;
    private final SkinDetectionStrategy defaultStrategy;

    public DetectionStrategyFactory(List<SkinDetectionStrategy> strategiesList) {
        this.strategies = strategiesList.stream()
                .collect(Collectors.toMap(
                        SkinDetectionStrategy::getName,
                        strategy -> strategy,
                        (existing, replacement) -> existing
                ));
        
        // Default to heuristic if not specified or found.
        this.defaultStrategy = strategies.getOrDefault("heuristic", strategiesList.get(0));
    }

    public SkinDetectionStrategy getStrategy(String name) {
        if (name == null || name.isBlank()) {
            return defaultStrategy;
        }
        return strategies.getOrDefault(name.toLowerCase(), defaultStrategy);
    }
}
